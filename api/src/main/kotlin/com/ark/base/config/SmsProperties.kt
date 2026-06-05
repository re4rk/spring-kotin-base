package com.ark.base.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "sms")
data class SmsProperties(
    val apiKey: String = "",
    val apiSecret: String = "",
    val from: String = "",
)
