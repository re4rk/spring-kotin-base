package com.ark.base.notification.infrastructure

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class KakaoSender {
    private val log = LoggerFactory.getLogger(javaClass)

    fun send(
        to: String,
        message: String,
    ) {
        log.info("[Kakao] to={} | message={}", to, message)
    }
}
