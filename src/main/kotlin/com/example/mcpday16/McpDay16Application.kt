package com.example.mcpday16

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class McpDay16Application

fun main(args: Array<String>) {
    runApplication<McpDay16Application>(*args)
}
