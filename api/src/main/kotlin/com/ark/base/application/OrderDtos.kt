package com.ark.base.application

import com.ark.base.order.Order
import com.ark.base.order.OrderStatus
import com.ark.base.product.Product

data class OrderPlaceRequest(
    val userId: Long,
    val productId: Long,
    val quantity: Int,
) {
    fun toOrder(product: Product) =
        Order(
            userId = userId,
            productId = product.id,
            productName = product.name,
            quantity = quantity,
        )
}

data class OrderResponse(
    val id: Long,
    val userId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val status: OrderStatus,
) {
    companion object {
        fun from(order: Order) =
            OrderResponse(
                id = order.id,
                userId = order.userId,
                productId = order.productId,
                productName = order.productName,
                quantity = order.quantity,
                status = order.status,
            )
    }
}
