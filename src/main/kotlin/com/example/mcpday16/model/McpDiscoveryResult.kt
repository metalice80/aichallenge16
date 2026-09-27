package com.example.mcpday16.model

data class McpDiscoveryResult(
    val protocolVersion: String,
    val serverName: String,
    val serverVersion: String?,
    val tools: List<McpToolSummary>,
)

data class McpToolSummary(
    val name: String,
    val description: String?,
)
