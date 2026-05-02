package com.ark.base.order

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun place(
        userId: Long,
        productName: String,
        quantity: Int,
    ): Order {
        val order = orderRepository.save(Order(userId = userId, productName = productName, quantity = quantity))
        eventPublisher.publishEvent(OrderPlacedEvent(order.id, order.userId, order.productName, order.quantity))
        return order
    }
}
