package com.ark.base.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "slack")
data class SlackProperties(
    val webhookUrl: String = "",
    val defaultChannel: String = "#general",
)
