package com.ark.base.auth.password.reset

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash("password-reset")
data class PasswordResetToken(
    @Id val token: String,
    val userId: Long,
    @TimeToLive val ttl: Long = 3600,
)
