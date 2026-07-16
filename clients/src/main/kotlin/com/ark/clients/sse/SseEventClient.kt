package com.ark.clients.sse

import com.ark.base.notification.sse.SseClient
import com.ark.base.notification.sse.SseLog
import com.ark.base.notification.sse.SseLogRepository
import com.ark.base.notification.sse.SseRegisterResult
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Component
class SseEventClient(
    private val sseLogRepository: SseLogRepository,
) : SseClient {
    private val log = LoggerFactory.getLogger(javaClass)
    private val emitters = ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>>()

    override fun register(
        userId: Long,
        eventType: String,
        data: String,
    ): SseRegisterResult {
        val emitter = registerEmitter(userId)
        runCatching {
            emitter.send(SseEmitter.event().name(eventType).data(data))
        }.onFailure {
            emitters[userId]?.remove(emitter)
            log.warn("SSE initial send failed userId={} eventType={} error={}", userId, eventType, it.message)
        }

        return SseRegisterResult(
            userId = userId,
            streamHandle = emitter,
        )
    }

    override fun send(
        userId: Long,
        eventType: String,
        data: String,
    ) {
        log.info("Sending SSE userId={} eventType={}", userId, eventType)
        val sseLog = sseLogRepository.save(SseLog(userId = userId, eventType = eventType, data = data))
        try {
            val delivered = sendToEmitters(userId, eventType, data)
            if (delivered) {
                sseLog.markDelivered()
                log.info("SSE delivered userId={} eventType={}", userId, eventType)
            } else {
                sseLog.markMissed()
                log.info("SSE missed (no active connection) userId={} eventType={}", userId, eventType)
            }
        } catch (e: Exception) {
            sseLog.markFailed(e.message ?: e.javaClass.simpleName)
            log.error("SSE failed userId={} eventType={} error={}", userId, eventType, e.message, e)
        } finally {
            sseLogRepository.save(sseLog)
        }
    }

    fun activeConnectionCount(): Int = emitters.values.sumOf { it.size }

    private fun registerEmitter(userId: Long): SseEmitter {
        val emitter = SseEmitter(TIMEOUT_MS)
        emitters.getOrPut(userId) { CopyOnWriteArrayList() }.add(emitter)

        val remove = Runnable { emitters[userId]?.remove(emitter) }
        emitter.onCompletion(remove)
        emitter.onTimeout(remove)
        emitter.onError { remove.run() }

        log.debug("SSE registered userId={} activeConnections={}", userId, activeConnectionCount())
        return emitter
    }

    private fun sendToEmitters(
        userId: Long,
        eventType: String,
        data: String,
    ): Boolean {
        val userEmitters = emitters[userId]?.takeIf { it.isNotEmpty() } ?: return false
        userEmitters.forEach { emitter ->
            runCatching {
                emitter.send(SseEmitter.event().name(eventType).data(data))
            }.onFailure {
                emitters[userId]?.remove(emitter)
                log.warn("SSE send failed userId={} eventType={} error={}", userId, eventType, it.message)
            }
        }
        return true
    }

    companion object {
        private const val TIMEOUT_MS = 30 * 60 * 1000L
    }
}
