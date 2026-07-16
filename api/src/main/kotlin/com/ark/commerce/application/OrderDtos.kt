package com.ark.commerce.application

import com.ark.commerce.order.Order
import com.ark.commerce.order.OrderStatus
import com.ark.commerce.product.Product

data class OrderPlaceRequest(
    val productId: Long,
    val skuId: Long,
    val quantity: Int,
) {
    fun toOrder(
        product: Product,
        userId: Long,
    ) = Order(
        userId = userId,
        productId = product.id,
        skuId = skuId,
        productName = product.name,
        quantity = quantity,
    )
}

data class OrderResponse(
    val id: Long,
    val userId: Long,
    val productId: Long,
    val skuId: Long,
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
                skuId = order.skuId,
                productName = order.productName,
                quantity = order.quantity,
                status = order.status,
            )
    }
}
