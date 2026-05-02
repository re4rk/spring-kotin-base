package com.ark.base.notification.infrastructure

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EmailSender {

    private val log = LoggerFactory.getLogger(javaClass)

    fun send(to: String, subject: String, body: String) {
        log.info("[Email] to={} | subject={} | body={}", to, subject, body)
    }
}
