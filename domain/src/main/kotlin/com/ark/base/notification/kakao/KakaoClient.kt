package com.ark.base.notification.kakao

interface KakaoClient {
    fun send(
        to: String,
        message: String,
    )
}
