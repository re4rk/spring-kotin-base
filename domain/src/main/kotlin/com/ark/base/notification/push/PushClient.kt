package com.ark.base.notification.push

interface PushClient {
    fun send(
        to: String,
        title: String,
        body: String,
    )
}
