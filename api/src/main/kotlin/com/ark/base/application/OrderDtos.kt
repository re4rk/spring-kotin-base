package com.ark.base.application

import com.ark.base.order.Order

data class OrderPlaceRequest(
    val userId: Long,
    val productName: String,
    val quantity: Int,
) {
    fun toOrder() = Order(userId = userId, productName = productName, quantity = quantity)
}

data class OrderResponse(
    val id: Long,
    val userId: Long,
    val productName: String,
    val quantity: Int,
) {
    companion object {
        fun from(order: Order) =
            OrderResponse(id = order.id, userId = order.userId, productName = order.productName, quantity = order.quantity)
    }
}
