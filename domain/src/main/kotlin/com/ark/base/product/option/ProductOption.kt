package com.ark.base.product.option

import com.ark.base.common.BaseEntity
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Version

@Entity
class ProductOption(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_group_id")
    val optionGroup: ProductOptionGroup,
    var name: String,
    var extraPrice: Long = 0,
    var stock: Int,
    var sortOrder: Int = 0,
    @Version
    val version: Long = 0,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    fun decreaseStock(quantity: Int) {
        if (stock < quantity) throw BaseException(ErrorCode.STOCK_INSUFFICIENT)
        stock -= quantity
    }

    fun increaseStock(quantity: Int) {
        stock += quantity
    }
}
