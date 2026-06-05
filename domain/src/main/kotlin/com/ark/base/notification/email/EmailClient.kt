package com.ark.base.notification.email

interface EmailClient {
    fun send(
        to: String,
        subject: String,
        body: String,
    )
}
