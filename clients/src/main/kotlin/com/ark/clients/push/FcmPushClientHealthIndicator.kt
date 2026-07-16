package com.ark.clients.push

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["management.health.fcm.enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
class FcmPushClientHealthIndicator : HealthIndicator {
    override fun health(): Health =
        // TODO: FCM 연동 후 실제 연결 검증으로 교체
        Health
            .up()
            .withDetail("provider", "FCM")
            .withDetail("status", "not integrated")
            .build()
}
