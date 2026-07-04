package com.ark.base.auth.refreshToken

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class RedisRefreshTokenRepository(
    private val redisTemplate: StringRedisTemplate,
) : RefreshTokenRepository {
    override fun issue(userId: Long): RefreshToken {
        val token =
            redisTemplate.execute(
                ISSUE_SCRIPT,
                emptyList(),
                userId.toString(),
                UUID.randomUUID().toString(),
                TTL_SECONDS.toString(),
                GRAVEYARD_TTL_SECONDS.toString(),
            ) ?: error("Failed to issue refresh token for userId=$userId")
        return RefreshToken(token = token, userId = userId)
    }

    override fun consume(token: String): RefreshTokenConsumeResult {
        val result =
            redisTemplate.execute(
                CONSUME_SCRIPT,
                emptyList(),
                token,
                TTL_SECONDS.toString(),
            )
        return when {
            result == null -> RefreshTokenConsumeResult.Invalid
            result == EXPIRED_MARKER -> RefreshTokenConsumeResult.Expired
            result.startsWith(REUSED_PREFIX) ->
                RefreshTokenConsumeResult.Reused(userId = result.removePrefix(REUSED_PREFIX).toLong())
            else -> RefreshTokenConsumeResult.Success(userId = result.toLong())
        }
    }

    override fun revokeAll(userId: Long) {
        redisTemplate.execute(
            REVOKE_ALL_SCRIPT,
            emptyList(),
            userId.toString(),
        )
    }

    companion object {
        private const val TTL_SECONDS = 604_800L
        private const val GRAVEYARD_TTL_SECONDS = TTL_SECONDS + 86_400L
        private const val REUSED_PREFIX = "REUSED:"
        private const val EXPIRED_MARKER = "EXPIRED"
        private const val KEYSPACE = "refresh-token"
        private const val USED_KEYSPACE = "refresh-token-used"
        private const val GRAVEYARD_KEYSPACE = "refresh-token-graveyard"
        private const val ENTITY_CLASS = "com.ark.base.auth.refresh_token.RefreshToken"
        private const val USED_ENTITY_CLASS = "com.ark.base.auth.refresh_token.UsedRefreshToken"

        private val ISSUE_SCRIPT =
            DefaultRedisScript<String>().apply {
                resultType = String::class.java
                setScriptText(
                    """
                    local userId = ARGV[1]
                    local newToken = ARGV[2]
                    local ttl = tonumber(ARGV[3])
                    local graveyardTtl = tonumber(ARGV[4])
                    local userIndexKey = '$KEYSPACE:userId:' .. userId
                    local oldTokens = redis.call('SMEMBERS', userIndexKey)
                    for _, oldToken in ipairs(oldTokens) do
                      redis.call('DEL', '$KEYSPACE:' .. oldToken)
                      redis.call('DEL', '$USED_KEYSPACE:' .. oldToken)
                      redis.call('DEL', '$GRAVEYARD_KEYSPACE:' .. oldToken)
                    end
                    redis.call('DEL', userIndexKey)
                    local entityKey = '$KEYSPACE:' .. newToken
                    redis.call(
                      'HSET',
                      entityKey,
                      'userId',
                      userId,
                      'ttl',
                      ttl,
                      '_class',
                      '$ENTITY_CLASS'
                    )
                    redis.call('EXPIRE', entityKey, ttl)
                    redis.call('SET', '$GRAVEYARD_KEYSPACE:' .. newToken, userId, 'EX', graveyardTtl)
                    redis.call('SADD', userIndexKey, newToken)
                    redis.call('EXPIRE', userIndexKey, ttl)
                    return newToken
                    """.trimIndent(),
                )
            }

        private val CONSUME_SCRIPT =
            DefaultRedisScript<String>().apply {
                resultType = String::class.java
                setScriptText(
                    """
                    local token = ARGV[1]
                    local ttl = tonumber(ARGV[2])
                    local entityKey = '$KEYSPACE:' .. token
                    local usedEntityKey = '$USED_KEYSPACE:' .. token
                    local userId = redis.call('HGET', entityKey, 'userId')
                    if userId then
                      redis.call('DEL', entityKey)
                      redis.call('DEL', '$GRAVEYARD_KEYSPACE:' .. token)
                      redis.call('SREM', '$KEYSPACE:userId:' .. userId, token)
                      redis.call(
                        'HSET',
                        usedEntityKey,
                        'userId',
                        userId,
                        'ttl',
                        ttl,
                        '_class',
                        '$USED_ENTITY_CLASS'
                      )
                      redis.call('EXPIRE', usedEntityKey, ttl)
                      return userId
                    end
                    local reusedUserId = redis.call('HGET', usedEntityKey, 'userId')
                    if reusedUserId then
                      return '$REUSED_PREFIX' .. reusedUserId
                    end
                    if redis.call('EXISTS', '$GRAVEYARD_KEYSPACE:' .. token) == 1 then
                      return '$EXPIRED_MARKER'
                    end
                    return nil
                    """.trimIndent(),
                )
            }

        private val REVOKE_ALL_SCRIPT =
            DefaultRedisScript<String>().apply {
                resultType = String::class.java
                setScriptText(
                    """
                    local userId = ARGV[1]
                    local userIndexKey = '$KEYSPACE:userId:' .. userId
                    local tokens = redis.call('SMEMBERS', userIndexKey)
                    for _, token in ipairs(tokens) do
                      redis.call('DEL', '$KEYSPACE:' .. token)
                      redis.call('DEL', '$USED_KEYSPACE:' .. token)
                      redis.call('DEL', '$GRAVEYARD_KEYSPACE:' .. token)
                    end
                    redis.call('DEL', userIndexKey)
                    if #tokens > 0 then
                      return tokens[1]
                    end
                    return nil
                    """.trimIndent(),
                )
            }
    }
}
