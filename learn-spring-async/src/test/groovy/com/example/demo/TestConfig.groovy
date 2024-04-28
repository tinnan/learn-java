package com.example.demo

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertySource

@TestConfiguration
class TestConfig {
    @Bean
    def objectMapper() {
        return new ObjectMapper();
    }
}
