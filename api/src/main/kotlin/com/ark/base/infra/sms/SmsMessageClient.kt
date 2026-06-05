package com.ark.base.infra.sms

import com.ark.base.notification.sms.SmsClient
import com.ark.base.notification.sms.SmsLog
import com.ark.base.notification.sms.SmsLogRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SmsMessageClient(
    private val smsLogRepository: SmsLogRepository,
) : SmsClient {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(
        to: String,
        message: String,
    ) {
        log.info("Sending sms to={}", to)
        val smsLog =
            smsLogRepository.save(
                SmsLog(recipient = to, message = message),
            )
        try {
            // TODO: SMS API 연동 (Twilio, NHN Cloud, etc.)
            smsLog.markSuccess(null)
            log.info("Sms sent to={}", to)
        } catch (e: Exception) {
            smsLog.markFailed(e.message ?: e.javaClass.simpleName)
            log.error("Failed to send sms to={} error={}", to, e.message, e)
            throw e
        } finally {
            smsLogRepository.save(smsLog)
        }
    }
}
