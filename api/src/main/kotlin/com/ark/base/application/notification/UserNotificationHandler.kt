package com.ark.base.application.notification

import com.ark.base.auth.PasswordResetRequestedEvent
import com.ark.base.notification.EmailSender
import com.ark.base.notification.KakaoSender
import com.ark.base.user.UserRegisteredEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserNotificationHandler(
    private val emailSender: EmailSender,
    private val kakaoSender: KakaoSender,
) {
    @TransactionalEventListener
    fun handle(event: PasswordResetRequestedEvent) {
        emailSender.send(
            to = event.email,
            subject = "비밀번호 재설정",
            body = "비밀번호 재설정 토큰: ${event.resetToken}",
        )
    }

    @TransactionalEventListener
    fun handle(event: UserRegisteredEvent) {
        emailSender.send(
            to = event.email,
            subject = "가입을 환영합니다!",
            body = "${event.name}님, 회원가입이 완료되었습니다.",
        )
        kakaoSender.send(
            to = event.email,
            message = "${event.name}님, 회원가입이 완료되었습니다.",
        )
    }
}
