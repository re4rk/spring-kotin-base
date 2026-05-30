package com.ark.base.auth

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash("refresh-token")
data class RefreshToken(
    @Id val token: String,
    val userId: Long,
    @TimeToLive val ttl: Long = 604_800,
)
