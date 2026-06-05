package com.ark.base.application.notification

import com.ark.base.notification.KakaoSender
import com.ark.base.notification.email.EmailClient
import com.ark.base.product.ProductApprovedEvent
import com.ark.base.product.ProductCreatedEvent
import com.ark.base.product.ProductDiscontinuedEvent
import com.ark.base.product.ProductMarkedSoldOutEvent
import com.ark.base.product.ProductRejectedEvent
import com.ark.base.product.ProductSaleResumedEvent
import com.ark.base.product.ProductSubmittedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ProductNotificationHandler(
    private val emailClient: EmailClient,
    private val kakaoSender: KakaoSender,
) {
    @TransactionalEventListener
    fun handle(event: ProductCreatedEvent) {
        notify(
            subject = "[상품 등록] ${event.productName}",
            message = "${event.productName} 상품이 등록되었습니다. (상품번호: ${event.productId}, 상태: ${event.status})",
        )
    }

    @TransactionalEventListener
    fun handle(event: ProductSubmittedEvent) {
        notify(
            subject = "[검수 요청] ${event.productName}",
            message = "${event.productName} 상품 검수가 요청되었습니다. (상품번호: ${event.productId})",
        )
    }

    @TransactionalEventListener
    fun handle(event: ProductApprovedEvent) {
        notify(
            subject = "[판매 승인] ${event.productName}",
            message = "${event.productName} 상품 판매가 승인되었습니다. (상품번호: ${event.productId})",
        )
    }

    @TransactionalEventListener
    fun handle(event: ProductRejectedEvent) {
        notify(
            subject = "[검수 반려] ${event.productName}",
            message = "${event.productName} 상품 검수가 반려되었습니다. (상품번호: ${event.productId})",
        )
    }

    @TransactionalEventListener
    fun handle(event: ProductMarkedSoldOutEvent) {
        notify(
            subject = "[품절] ${event.productName}",
            message = "${event.productName} 상품이 품절 처리되었습니다. (상품번호: ${event.productId})",
        )
    }

    @TransactionalEventListener
    fun handle(event: ProductSaleResumedEvent) {
        notify(
            subject = "[판매 재개] ${event.productName}",
            message = "${event.productName} 상품 판매가 재개되었습니다. (상품번호: ${event.productId})",
        )
    }

    @TransactionalEventListener
    fun handle(event: ProductDiscontinuedEvent) {
        notify(
            subject = "[판매 중단] ${event.productName}",
            message = "${event.productName} 상품 판매가 중단되었습니다. (상품번호: ${event.productId})",
        )
    }

    private fun notify(
        subject: String,
        message: String,
    ) {
        emailClient.send(
            to = "admin@example.com",
            subject = subject,
            body = message,
        )
        kakaoSender.send(
            to = "admin",
            message = message,
        )
    }
}
