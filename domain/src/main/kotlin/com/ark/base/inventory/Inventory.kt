package com.ark.base.inventory

import com.ark.base.common.BaseAggregateEntity
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Inventory(
    val productId: Long,
    var stock: Int,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseAggregateEntity<Inventory>() {
    fun decrease(quantity: Int) {
        if (stock < quantity) throw BaseException(ErrorCode.STOCK_INSUFFICIENT)
        stock -= quantity
        if (stock <= 0) {
            registerEvent(StockSoldOutEvent(id, productId))
        }
    }
}
