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
    fun place(command: OrderPlaceCommand): Order {
        val order = orderRepository.save(command.toOrder())
        eventPublisher.publishEvent(OrderPlacedEvent(order.id, order.userId, order.productName, order.quantity))
        return order
    }
}
