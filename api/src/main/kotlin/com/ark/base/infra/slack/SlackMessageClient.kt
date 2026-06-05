package com.ark.base.infra.slack

import com.ark.base.config.SlackProperties
import com.ark.base.notification.slack.SlackClient
import com.ark.base.notification.slack.SlackLog
import com.ark.base.notification.slack.SlackLogRepository
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SlackMessageClient(
    private val slackLogRepository: SlackLogRepository,
    private val slackProperties: SlackProperties,
) : SlackClient {
    private val log = LoggerFactory.getLogger(javaClass)
    private val restClient = RestClient.create()

    override fun send(
        channel: String,
        message: String,
    ) {
        log.info("Sending slack channel={}", channel)
        val slackLog =
            slackLogRepository.save(
                SlackLog(channel = channel, message = message),
            )
        try {
            restClient
                .post()
                .uri(slackProperties.webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapOf("channel" to channel, "text" to message))
                .retrieve()
                .toBodilessEntity()
            slackLog.markSuccess(null)
            log.info("Slack sent channel={}", channel)
        } catch (e: Exception) {
            slackLog.markFailed(e.message ?: e.javaClass.simpleName)
            log.error("Failed to send slack channel={} error={}", channel, e.message, e)
            throw e
        } finally {
            slackLogRepository.save(slackLog)
        }
    }
}
