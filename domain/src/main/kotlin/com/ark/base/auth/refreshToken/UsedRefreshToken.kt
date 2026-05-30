package com.ark.base.auth.refreshToken

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash("refresh-token-used")
data class UsedRefreshToken(
    @Id val token: String,
    val userId: Long,
    @TimeToLive val ttl: Long = 604_800,
)
