package com.ark.base.order

enum class OrderStatus {
    PLACED,
    CONFIRMED,
    SHIPPING,
    DELIVERED,
    CANCELLED,
    ;

    fun canTransitionTo(target: OrderStatus): Boolean =
        when (this) {
            PLACED -> target in setOf(CONFIRMED, CANCELLED)
            CONFIRMED -> target in setOf(SHIPPING, CANCELLED)
            SHIPPING -> target == DELIVERED
            DELIVERED, CANCELLED -> false
        }

    val isCancellable: Boolean get() = this in setOf(PLACED, CONFIRMED)
}
