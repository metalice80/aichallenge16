package com.example.mcpday16.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("app.mcp")
data class McpProperties(
    val command: String = "npx",
    val arguments: List<String> = listOf("-y", "@modelcontextprotocol/server-everything"),
    val requestTimeout: Duration = Duration.ofSeconds(20),
    val demoEnabled: Boolean = true,
) {
    init {
        require(command.isNotBlank()) { "app.mcp.command must not be blank" }
        require(arguments.all(String::isNotBlank)) { "app.mcp.arguments must not contain blank values" }
        require(!requestTimeout.isZero && !requestTimeout.isNegative) {
            "app.mcp.request-timeout must be positive"
        }
    }
}
