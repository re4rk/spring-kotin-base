package com.ark.clients.sms

import com.ark.clients.config.SmsProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["management.health.sms.enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
class SmsClientHealthIndicator(
    private val smsProperties: SmsProperties,
) : HealthIndicator {
    override fun health(): Health =
        if (smsProperties.apiKey.isNotBlank()) {
            Health
                .up()
                .withDetail("provider", "SMS")
                .withDetail("from", smsProperties.from)
                .build()
        } else {
            Health
                .unknown()
                .withDetail("provider", "SMS")
                .withDetail("reason", "apiKey not configured")
                .build()
        }
}
