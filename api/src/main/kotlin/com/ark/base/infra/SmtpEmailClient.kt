package com.ark.base.infra

import com.ark.base.common.EmailProperties
import com.ark.base.notification.email.EmailClient
import com.ark.base.notification.email.EmailLog
import com.ark.base.notification.email.EmailLogRepository
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Component
class SmtpEmailClient(
    private val javaMailSender: JavaMailSender,
    private val emailProperties: EmailProperties,
    private val emailLogRepository: EmailLogRepository,
) : EmailClient {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(
        to: String,
        subject: String,
        body: String,
    ) {
        log.info("Sending email to={} subject={}", to, subject)
        val emailLog =
            emailLogRepository.save(
                EmailLog(
                    receiverEmail = to,
                    senderEmail = emailProperties.from,
                    subject = subject,
                ),
            )
        try {
            val message = javaMailSender.createMimeMessage()
            MimeMessageHelper(message, false, "UTF-8").apply {
                setFrom(emailProperties.from)
                setTo(to)
                setSubject(subject)
                setText(body)
            }
            javaMailSender.send(message)
            emailLog.markSuccess(message.messageID)
            log.info("Email sent to={} subject={} messageId={}", to, subject, message.messageID)
        } catch (e: Exception) {
            emailLog.markFailed(e.message ?: e.javaClass.simpleName)
            log.error("Failed to send email to={} subject={} error={}", to, subject, e.message, e)
            throw e
        } finally {
            emailLogRepository.save(emailLog)
        }
    }
}
