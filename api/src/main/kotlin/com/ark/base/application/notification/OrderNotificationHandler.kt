package com.ark.base.application.notification

import com.ark.base.notification.EmailSender
import com.ark.base.notification.KakaoSender
import com.ark.base.order.OrderPlacedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrderNotificationHandler(
    private val emailSender: EmailSender,
    private val kakaoSender: KakaoSender,
) {
    @TransactionalEventListener
    fun handle(event: OrderPlacedEvent) {
        emailSender.send(
            to = "user-${event.userId}@example.com",
            subject = "주문이 완료되었습니다!",
            body = "${event.productName} ${event.quantity}개 주문 완료 (주문번호: ${event.orderId})",
        )
        kakaoSender.send(
            to = "user-${event.userId}",
            message = "${event.productName} ${event.quantity}개 주문 완료! (주문번호: ${event.orderId})",
        )
    }
}
