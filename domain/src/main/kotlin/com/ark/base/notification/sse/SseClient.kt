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
