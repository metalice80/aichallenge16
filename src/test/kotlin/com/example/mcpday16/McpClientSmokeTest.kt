package com.example.mcpday16

import com.example.mcpday16.service.McpClientService
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.TimeUnit

@Tag("integration")
@Timeout(value = 90, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = [
        "app.mcp.demo-enabled=false",
        "app.mcp.request-timeout=30s",
    ],
)
class McpClientSmokeTest @Autowired constructor(
    private val mcpClientService: McpClientService,
) {
    @Test
    fun `initialize then list tools against real everything server`() {
        val result = mcpClientService.discover()

        assertFalse(result.protocolVersion.isBlank(), "Negotiated protocol version must not be blank")
        assertFalse(result.serverName.isBlank(), "Server name must not be blank")
        assertNotNull(result.serverVersion, "Server version must be present")
        assertFalse(result.tools.isEmpty(), "Everything server must expose at least one tool")
        assertFalse(result.tools.any { it.name.isBlank() }, "Every tool name must be non-blank")
    }
}
