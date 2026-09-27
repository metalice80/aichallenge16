package com.example.mcpday16.runner

import com.example.mcpday16.service.McpClientService
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "app.mcp", name = ["demo-enabled"], havingValue = "true", matchIfMissing = true)
class McpDemoRunner(
    private val mcpClientService: McpClientService,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        val result = mcpClientService.discover()

        logger.info(
            "MCP discovery complete: protocol={}, server={} ({})",
            result.protocolVersion,
            result.serverName,
            result.serverVersion,
        )
        logger.info("MCP tools available: count={}", result.tools.size)
        result.tools.forEach { tool ->
            logger.info("MCP tool: name={}, description={}", tool.name, tool.description ?: "")
        }
    }

    private companion object {
        private val logger = LoggerFactory.getLogger(McpDemoRunner::class.java)
    }
}
