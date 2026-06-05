package com.ark.base.notification.sse

interface SseClient {
    fun register(
        userId: Long,
        eventType: String,
        data: String,
    ): SseRegisterResult

    fun send(
        userId: Long,
        eventType: String,
        data: String,
    )
}

data class SseRegisterResult(
    val userId: Long,
    // infra transport 객체를 감춘 opaque handle
    val streamHandle: Any,
)
