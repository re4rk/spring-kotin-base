package com.ark.base.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.json.JsonMapper

@Configuration
class JacksonConfig {
    @Bean
    fun jsonMapper(): JsonMapper = JsonMapper.builder().build()
}
