package com.ark.base.application

import com.ark.base.inventory.Inventory
import com.ark.base.product.Product
import com.ark.base.product.ProductQueryFilter
import com.ark.base.product.ProductStatus

data class ProductQueryFilterRequest(
    val status: ProductStatus? = null,
    val name: String? = null,
    val minPrice: Long? = null,
    val maxPrice: Long? = null,
) {
    fun toQueryFilter() =
        ProductQueryFilter(
            status = status,
            name = name?.takeIf { it.isNotBlank() },
            minPrice = minPrice,
            maxPrice = maxPrice,
        )
}

data class ProductCreateRequest(
    val name: String,
    val price: Long,
    val initialStock: Int,
) {
    fun toProduct() = Product(name = name, price = price)

    fun toInventory(product: Product) = Inventory(productId = product.id, stock = initialStock)
}

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val stock: Int,
    val status: ProductStatus,
) {
    companion object {
        fun from(
            product: Product,
            stock: Int,
        ) = ProductResponse(
            id = product.id,
            name = product.name,
            price = product.price,
            stock = stock,
            status = product.status,
        )
    }
}
