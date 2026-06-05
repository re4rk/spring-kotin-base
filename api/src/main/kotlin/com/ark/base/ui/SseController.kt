package com.ark.base.ui

import com.ark.base.common.CurrentUser
import com.ark.base.notification.sse.SseClient
import com.ark.base.notification.sse.SseRegisterResult
import com.ark.base.ui.auth.InjectCurrentUser
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/sse")
class SseController(
    private val sseClient: SseClient,
) {
    @GetMapping("/connect", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun connect(
        @InjectCurrentUser currentUser: CurrentUser,
    ): SseEmitter =
        sseClient
            .register(
                userId = currentUser.requireUserId(),
                eventType = "connect",
                data = "connected",
            ).toEmitter()

    private fun SseRegisterResult.toEmitter(): SseEmitter =
        streamHandle as? SseEmitter
            ?: error("Invalid SSE stream handle type: ${streamHandle::class.qualifiedName}")
}
