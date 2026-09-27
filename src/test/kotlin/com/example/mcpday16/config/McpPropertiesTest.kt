package com.example.mcpday16.config

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.Duration

class McpPropertiesTest {
    @Test
    fun `default settings are valid`() {
        assertDoesNotThrow { McpProperties() }
    }

    @Test
    fun `blank command is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            McpProperties(command = " ")
        }
    }

    @Test
    fun `blank argument is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            McpProperties(arguments = listOf("-y", ""))
        }
    }

    @Test
    fun `non-positive timeout is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            McpProperties(requestTimeout = Duration.ZERO)
        }
    }
}
