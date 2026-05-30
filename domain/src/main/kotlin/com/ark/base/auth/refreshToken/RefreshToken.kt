package com.ark.base.auth.refreshToken

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed

@RedisHash("refresh-token")
data class RefreshToken(
    @Id val token: String,
    @Indexed val userId: Long,
    @TimeToLive val ttl: Long = 604_800,
)
