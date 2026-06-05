package com.ark.base.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth")
data class OAuthProperties(
    val google: ProviderProperties = ProviderProperties(),
    val kakao: ProviderProperties = ProviderProperties(),
    val naver: ProviderProperties = ProviderProperties(),
) {
    data class ProviderProperties(
        val clientId: String = "",
        val clientSecret: String = "",
        val redirectUri: String = "",
    )
}
