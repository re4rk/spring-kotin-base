package com.ark.clients.slack

import com.ark.clients.config.SlackProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["management.health.slack.enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
class SlackClientHealthIndicator(
    private val slackProperties: SlackProperties,
) : HealthIndicator {
    override fun health(): Health =
        if (slackProperties.webhookUrl.isNotBlank()) {
            Health
                .up()
                .withDetail("provider", "Slack")
                .withDetail("defaultChannel", slackProperties.defaultChannel)
                .build()
        } else {
            Health
                .unknown()
                .withDetail("provider", "Slack")
                .withDetail("reason", "webhookUrl not configured")
                .build()
        }
}
