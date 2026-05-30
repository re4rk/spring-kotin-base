package com.ark.base.inventory

import com.ark.base.common.BaseAggregateEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Inventory(
    val productName: String,
    var stock: Int,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseAggregateEntity<Inventory>() {

    fun decrease(quantity: Int) {
        stock -= quantity
        if (stock <= 0) {
            registerEvent(StockSoldOutEvent(id, productName))
        }
    }
}
