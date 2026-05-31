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
    val category: String? = null,
) {
    fun toQueryFilter() =
        ProductQueryFilter(
            status = status,
            name = name?.takeIf { it.isNotBlank() },
            minPrice = minPrice,
            maxPrice = maxPrice,
            category = category?.takeIf { it.isNotBlank() },
        )
}

data class ProductUpdateRequest(
    val name: String,
    val price: Long,
    val description: String? = null,
    val category: String? = null,
    val thumbnailUrl: String? = null,
)

data class ProductCreateRequest(
    val name: String,
    val price: Long,
    val initialStock: Int,
    val description: String? = null,
    val category: String? = null,
    val thumbnailUrl: String? = null,
) {
    fun toProduct() = Product(name = name, price = price, description = description, category = category, thumbnailUrl = thumbnailUrl)

    fun toInventory(product: Product) = Inventory(productId = product.id, stock = initialStock)
}

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val stock: Int,
    val status: ProductStatus,
    val description: String?,
    val category: String?,
    val thumbnailUrl: String?,
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
            description = product.description,
            category = product.category,
            thumbnailUrl = product.thumbnailUrl,
        )
    }
}
