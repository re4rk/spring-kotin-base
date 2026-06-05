package com.ark.base.application

import com.ark.base.notification.sse.SseClient
import com.ark.base.notification.sse.SseRegisterResult
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class SseService(
    private val sseClient: SseClient,
) {
    fun connect(userId: Long): SseEmitter =
        sseClient
            .register(
                userId = userId,
                eventType = "connect",
                data = "connected",
            ).toEmitter()

    private fun SseRegisterResult.toEmitter(): SseEmitter =
        streamHandle as? SseEmitter
            ?: error("Invalid SSE stream handle type: ${streamHandle::class.qualifiedName}")
}
