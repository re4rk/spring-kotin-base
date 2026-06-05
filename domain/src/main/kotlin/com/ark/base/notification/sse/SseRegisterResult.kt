package com.ark.base.notification.sse

data class SseRegisterResult(
    val userId: Long,
    // infra transport 객체를 감춘 opaque handle
    val streamHandle: Any,
)
