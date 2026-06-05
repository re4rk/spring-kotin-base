package com.ark.base.infra.push

import com.ark.base.notification.push.PushClient
import com.ark.base.notification.push.PushLog
import com.ark.base.notification.push.PushLogRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FcmPushClient(
    private val pushLogRepository: PushLogRepository,
) : PushClient {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(
        to: String,
        title: String,
        body: String,
    ) {
        log.info("Sending push to={} title={}", to, title)
        val pushLog =
            pushLogRepository.save(
                PushLog(recipient = to, title = title, body = body),
            )
        try {
            // TODO: FCM 연동
            pushLog.markSuccess(null)
            log.info("Push sent to={} title={}", to, title)
        } catch (e: Exception) {
            pushLog.markFailed(e.message ?: e.javaClass.simpleName)
            log.error("Failed to send push to={} title={} error={}", to, title, e.message, e)
            throw e
        } finally {
            pushLogRepository.save(pushLog)
        }
    }
}
