package com.ark.base.application

import com.ark.base.inventory.InventoryRepository
import com.ark.base.inventory.getByProductId
import com.ark.base.order.OrderRepository
import com.ark.base.order.getById
import com.ark.base.product.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val inventoryRepository: InventoryRepository,
) {
    @Transactional
    fun place(request: OrderPlaceRequest): OrderResponse {
        val product = productRepository.getById(request.productId)
        val inventory = inventoryRepository.getByProductId(request.productId)
        inventory.decrease(request.quantity)
        val order = orderRepository.save(request.toOrder(product))
        return OrderResponse.from(order)
    }

    @Transactional
    fun cancel(orderId: Long): OrderResponse {
        val order = orderRepository.getById(orderId)
        order.cancel()
        inventoryRepository.getByProductId(order.productId).increase(order.quantity)
        return OrderResponse.from(order)
    }

    @Transactional
    fun confirm(orderId: Long): OrderResponse {
        val order = orderRepository.getById(orderId)
        order.confirm()
        return OrderResponse.from(order)
    }

    @Transactional
    fun ship(orderId: Long): OrderResponse {
        val order = orderRepository.getById(orderId)
        order.ship()
        return OrderResponse.from(order)
    }

    @Transactional
    fun deliver(orderId: Long): OrderResponse {
        val order = orderRepository.getById(orderId)
        order.deliver()
        return OrderResponse.from(order)
    }
}
