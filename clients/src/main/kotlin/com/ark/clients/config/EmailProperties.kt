package com.ark.clients.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "email")
data class EmailProperties(
    val from: String = "noreply@base.local",
)
