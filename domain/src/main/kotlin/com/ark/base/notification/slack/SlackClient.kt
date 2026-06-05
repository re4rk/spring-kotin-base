package com.ark.base.notification.slack

interface SlackClient {
    fun send(
        channel: String,
        message: String,
    )
}
