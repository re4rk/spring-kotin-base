package com.ark.base.order

class OrderPlacedEvent(
    private val order: Order,
) {
    val orderId get() = order.id
    val userId get() = order.userId
    val productName get() = order.productName
    val quantity get() = order.quantity
}
