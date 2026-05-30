package com.ark.base.auth

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisPasswordResetTokenRepository(
    private val redisTemplate: StringRedisTemplate,
) : PasswordResetTokenRepository {
    override fun save(
        token: String,
        userId: Long,
    ) {
        redisTemplate.opsForValue().set(key(token), userId.toString(), 1, TimeUnit.HOURS)
    }

    override fun getUserId(token: String): Long? = redisTemplate.opsForValue().get(key(token))?.toLong()

    override fun delete(token: String) {
        redisTemplate.delete(key(token))
    }

    private fun key(token: String) = "password-reset:$token"
}
