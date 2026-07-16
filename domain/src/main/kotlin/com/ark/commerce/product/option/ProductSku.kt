package com.ark.commerce.product.option

import com.ark.base.common.BaseEntity
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.commerce.product.Product
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Version

@Entity
@Table(name = "commerce_product_sku")
class ProductSku(
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "commerce_product_sku_option",
        joinColumns = [JoinColumn(name = "sku_id")],
        inverseJoinColumns = [JoinColumn(name = "option_id")],
    )
    val options: MutableList<ProductOption> = mutableListOf(),
    var extraPrice: Long = 0,
    var stock: Int,
    @Version
    val version: Long = 0,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    lateinit var product: Product
        internal set

    fun decreaseStock(quantity: Int) {
        if (stock < quantity) throw BaseException(ErrorCode.STOCK_INSUFFICIENT)
        stock -= quantity
    }

    fun increaseStock(quantity: Int) {
        stock += quantity
    }
}
