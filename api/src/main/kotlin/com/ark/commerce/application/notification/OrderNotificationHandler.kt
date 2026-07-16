package com.ark.commerce.application.notification

import com.ark.base.notification.email.EmailClient
import com.ark.base.notification.kakao.KakaoClient
import com.ark.commerce.order.OrderCancelledEvent
import com.ark.commerce.order.OrderConfirmedEvent
import com.ark.commerce.order.OrderDeliveredEvent
import com.ark.commerce.order.OrderPlacedEvent
import com.ark.commerce.order.OrderShippedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrderNotificationHandler(
    private val emailClient: EmailClient,
    private val kakaoClient: KakaoClient,
) {
    @TransactionalEventListener
    fun handle(event: OrderPlacedEvent) {
        notify(
            userId = event.userId,
            subject = "주문이 완료되었습니다!",
            message = "${event.productName} ${event.quantity}개 주문 완료 (주문번호: ${event.orderId})",
        )
    }

    @TransactionalEventListener
    fun handle(event: OrderConfirmedEvent) {
        notify(
            userId = event.userId,
            subject = "주문이 확인되었습니다",
            message = "${event.productName} ${event.quantity}개 주문 확인 (주문번호: ${event.orderId})",
        )
    }

    @TransactionalEventListener
    fun handle(event: OrderShippedEvent) {
        notify(
            userId = event.userId,
            subject = "상품이 배송 중입니다",
            message = "${event.productName} ${event.quantity}개 배송 시작 (주문번호: ${event.orderId})",
        )
    }

    @TransactionalEventListener
    fun handle(event: OrderDeliveredEvent) {
        notify(
            userId = event.userId,
            subject = "배송이 완료되었습니다",
            message = "${event.productName} ${event.quantity}개 배송 완료 (주문번호: ${event.orderId})",
        )
    }

    @TransactionalEventListener
    fun handle(event: OrderCancelledEvent) {
        notify(
            userId = event.userId,
            subject = "주문이 취소되었습니다",
            message = "${event.productName} ${event.quantity}개 주문 취소 (주문번호: ${event.orderId})",
        )
    }

    private fun notify(
        userId: Long,
        subject: String,
        message: String,
    ) {
        emailClient.send(
            to = "user-$userId@example.com",
            subject = subject,
            body = message,
        )
        kakaoClient.send(
            to = "user-$userId",
            message = message,
        )
    }
}
