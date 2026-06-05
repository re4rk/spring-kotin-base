package com.ark.base.auth.oauth

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash("oauth-state")
data class OAuthState(
    @Id val state: String,
    val provider: String,
    val redirectUri: String,
    @TimeToLive val ttl: Long = 300,
)
