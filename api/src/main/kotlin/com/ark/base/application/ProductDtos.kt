package com.ark.base.application

import com.ark.base.product.Product

data class ProductCreateRequest(
    val name: String,
    val price: Long,
    val initialStock: Int,
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val stock: Int,
) {
    companion object {
        fun from(
            product: Product,
            stock: Int,
        ) = ProductResponse(id = product.id, name = product.name, price = product.price, stock = stock)
    }
}
