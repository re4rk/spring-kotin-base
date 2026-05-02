package com.ark.base.order

data class OrderPlacedEvent(
    val orderId: Long,
    val userId: Long,
    val productName: String,
    val quantity: Int,
)
