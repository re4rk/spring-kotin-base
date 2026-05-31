package com.ark.base.order

import com.ark.base.common.BaseAggregateEntity
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "orders")
class Order(
    val userId: Long,
    val productId: Long,
    val skuId: Long,
    val productName: String,
    val quantity: Int,
    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.PLACED,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseAggregateEntity<Order>() {
    init {
        registerEvent(OrderPlacedEvent(this))
    }

    fun confirm() {
        transitionTo(OrderStatus.CONFIRMED)
        registerEvent(OrderConfirmedEvent(this))
    }

    fun ship() {
        transitionTo(OrderStatus.SHIPPING)
        registerEvent(OrderShippedEvent(this))
    }

    fun deliver() {
        transitionTo(OrderStatus.DELIVERED)
        registerEvent(OrderDeliveredEvent(this))
    }

    fun cancel() {
        if (status == OrderStatus.CANCELLED) throw BaseException(ErrorCode.ORDER_ALREADY_CANCELLED)
        if (!status.isCancellable) throw BaseException(ErrorCode.ORDER_INVALID_STATUS)
        status = OrderStatus.CANCELLED
        registerEvent(OrderCancelledEvent(this))
    }

    private fun transitionTo(target: OrderStatus) {
        if (!status.canTransitionTo(target)) throw BaseException(ErrorCode.ORDER_INVALID_STATUS)
        status = target
    }
}
