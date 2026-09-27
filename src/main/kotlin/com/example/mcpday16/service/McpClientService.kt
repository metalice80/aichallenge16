package com.example.mcpday16.service

import com.example.mcpday16.config.McpProperties
import com.example.mcpday16.model.McpDiscoveryResult
import com.example.mcpday16.model.McpToolSummary
import io.modelcontextprotocol.client.McpClient
import io.modelcontextprotocol.client.McpSyncClient
import io.modelcontextprotocol.client.transport.ServerParameters
import io.modelcontextprotocol.client.transport.StdioClientTransport
import io.modelcontextprotocol.json.McpJsonDefaults
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicReference

@Service
class McpClientService(
    private val properties: McpProperties,
) {
    private val activeClient = AtomicReference<McpSyncClient?>()

    fun discover(): McpDiscoveryResult {
        check(activeClient.get() == null) { "MCP discovery is already running" }
        logger.info("Connecting to MCP server via STDIO: {}", properties.command)

        val serverParameters = ServerParameters.builder(properties.command)
            .args(properties.arguments)
            .build()
        val transport = StdioClientTransport(serverParameters, McpJsonDefaults.getMapper())
        val client = McpClient.sync(transport)
            .requestTimeout(properties.requestTimeout)
            .initializationTimeout(properties.requestTimeout)
            .build()

        check(activeClient.compareAndSet(null, client)) { "MCP discovery is already running" }
        var primaryFailure: Throwable? = null

        try {
            val initializeResult = client.initialize()
            val serverInfo = requireNotNull(initializeResult.serverInfo()) {
                "MCP initialize response did not include server information"
            }
            logger.info(
                "MCP initialized: protocol={}, server={} ({})",
                initializeResult.protocolVersion(),
                serverInfo.name(),
                serverInfo.version(),
            )

            val tools = client.listTools().tools().map { tool ->
                McpToolSummary(name = tool.name(), description = tool.description())
            }
            logger.info("MCP tools received: count={}", tools.size)

            return McpDiscoveryResult(
                protocolVersion = initializeResult.protocolVersion(),
                serverName = serverInfo.name(),
                serverVersion = serverInfo.version(),
                tools = tools,
            )
        } catch (error: Exception) {
            val failure = McpDiscoveryException(
                "MCP discovery failed using command '${properties.command}'",
                error,
            )
            primaryFailure = failure
            logger.error(failure.message, error)
            throw failure
        } finally {
            closeClient(client, primaryFailure)
        }
    }

    @PreDestroy
    fun close() {
        val client = activeClient.getAndSet(null) ?: return
        closeWithoutMasking(client, null)
    }

    private fun closeClient(client: McpSyncClient, primaryFailure: Throwable?) {
        if (!activeClient.compareAndSet(client, null)) {
            return
        }
        closeWithoutMasking(client, primaryFailure)
    }

    private fun closeWithoutMasking(client: McpSyncClient, primaryFailure: Throwable?) {
        val closeFailure = runCatching {
            check(client.closeGracefully()) { "MCP client did not close gracefully within the SDK timeout" }
        }.exceptionOrNull()

        if (closeFailure == null) {
            logger.info("MCP client closed")
            return
        }

        if (primaryFailure != null) {
            primaryFailure.addSuppressed(closeFailure)
            logger.error("MCP client closing failed after the discovery error", closeFailure)
            return
        }

        logger.error("MCP client closing failed", closeFailure)
        throw McpClientCloseException("MCP client closing failed", closeFailure)
    }

    private companion object {
        private val logger = LoggerFactory.getLogger(McpClientService::class.java)
    }
}

class McpDiscoveryException(message: String, cause: Throwable) : RuntimeException(message, cause)

class McpClientCloseException(message: String, cause: Throwable) : RuntimeException(message, cause)
