package com.ark.base.order

data class OrderPlaceRequest(
    val userId: Long,
    val productName: String,
    val quantity: Int,
)

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
