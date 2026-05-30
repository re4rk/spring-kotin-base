package com.ark.base.order

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {
    @Transactional
    fun place(request: OrderPlaceRequest): OrderResponse {
        val order = orderRepository.save(request.toOrder())
        return OrderResponse.from(order)
    }
}
