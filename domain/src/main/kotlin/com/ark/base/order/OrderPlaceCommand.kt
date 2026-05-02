package com.ark.base.order

data class OrderPlaceCommand(
    val userId: Long,
    val productName: String,
    val quantity: Int,
) {
    fun toOrder() = Order(userId = userId, productName = productName, quantity = quantity)
}
