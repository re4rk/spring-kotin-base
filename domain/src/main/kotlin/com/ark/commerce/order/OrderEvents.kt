package com.ark.commerce.order

class OrderPlacedEvent(
    private val order: Order,
) {
    val orderId get() = order.id
    val userId get() = order.userId
    val productId get() = order.productId
    val productName get() = order.productName
    val quantity get() = order.quantity
}

class OrderConfirmedEvent(
    private val order: Order,
) {
    val orderId get() = order.id
    val userId get() = order.userId
    val productId get() = order.productId
    val productName get() = order.productName
    val quantity get() = order.quantity
}

class OrderShippedEvent(
    private val order: Order,
) {
    val orderId get() = order.id
    val userId get() = order.userId
    val productId get() = order.productId
    val productName get() = order.productName
    val quantity get() = order.quantity
}

class OrderDeliveredEvent(
    private val order: Order,
) {
    val orderId get() = order.id
    val userId get() = order.userId
    val productId get() = order.productId
    val productName get() = order.productName
    val quantity get() = order.quantity
}

class OrderCancelledEvent(
    private val order: Order,
) {
    val orderId get() = order.id
    val userId get() = order.userId
    val productId get() = order.productId
    val productName get() = order.productName
    val quantity get() = order.quantity
}
