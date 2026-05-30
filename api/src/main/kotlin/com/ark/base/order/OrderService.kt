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
    fun place(request: OrderPlaceRequest): OrderResponse {
        val order = orderRepository.save(Order(userId = request.userId, productName = request.productName, quantity = request.quantity))
        eventPublisher.publishEvent(OrderPlacedEvent(order.id, order.userId, order.productName, order.quantity))
        return OrderResponse.from(order)
    }
}
