package com.ark.base.common

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "email")
data class EmailProperties(
    val from: String = "noreply@base.local",
)

@Configuration
@EnableConfigurationProperties(EmailProperties::class)
class EmailConfig
