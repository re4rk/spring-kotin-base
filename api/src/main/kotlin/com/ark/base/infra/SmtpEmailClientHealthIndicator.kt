package com.ark.base.infra

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["management.health.smtp.enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
class SmtpEmailClientHealthIndicator(
    private val javaMailSender: JavaMailSender,
) : HealthIndicator {
    override fun health(): Health {
        val impl = javaMailSender as JavaMailSenderImpl
        return runCatching {
            impl.testConnection()
            Health
                .up()
                .withDetail("host", impl.host ?: "unknown")
                .withDetail("port", impl.port)
                .build()
        }.getOrElse { ex ->
            Health
                .down(ex)
                .withDetail("host", impl.host ?: "unknown")
                .withDetail("port", impl.port)
                .build()
        }
    }
}
