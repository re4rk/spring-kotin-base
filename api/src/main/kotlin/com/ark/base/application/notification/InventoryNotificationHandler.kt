package com.ark.base.application.notification

import com.ark.base.inventory.StockSoldOutEvent
import com.ark.base.notification.EmailSender
import com.ark.base.notification.KakaoSender
import com.ark.base.product.ProductRepository
import com.ark.base.product.findByIdOrThrow
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class InventoryNotificationHandler(
    private val emailSender: EmailSender,
    private val kakaoSender: KakaoSender,
    private val productRepository: ProductRepository,
) {
    @TransactionalEventListener
    fun handle(event: StockSoldOutEvent) {
        val product = productRepository.findByIdOrThrow(event.productId)
        val productName = product.name
        emailSender.send(
            to = "admin@example.com",
            subject = "[재고 품절] $productName",
            body = "$productName 재고가 모두 소진되었습니다.",
        )
        kakaoSender.send(
            to = "admin",
            message = "[품절 알림] $productName 재고 소진",
        )
    }
}
