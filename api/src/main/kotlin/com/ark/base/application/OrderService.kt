package com.ark.base.application

import com.ark.base.inventory.InventoryRepository
import com.ark.base.inventory.getByProductId
import com.ark.base.order.OrderRepository
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
}
