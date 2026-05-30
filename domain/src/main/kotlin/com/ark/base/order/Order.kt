package com.ark.base.order

import com.ark.base.common.BaseAggregateEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "orders")
class Order(
    val userId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseAggregateEntity<Order>() {
    init {
        registerEvent(OrderPlacedEvent(this))
    }
}
