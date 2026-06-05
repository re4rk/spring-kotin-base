package com.ark.base.notification.sms

interface SmsClient {
    fun send(
        to: String,
        message: String,
    )
}
