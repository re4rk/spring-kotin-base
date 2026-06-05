package com.ark.base.infra.sse

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["management.health.sse.enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
class SseClientHealthIndicator(
    private val sseEventClient: SseEventClient,
) : HealthIndicator {
    override fun health(): Health =
        Health
            .up()
            .withDetail("activeConnections", sseEventClient.activeConnectionCount())
            .build()
}
