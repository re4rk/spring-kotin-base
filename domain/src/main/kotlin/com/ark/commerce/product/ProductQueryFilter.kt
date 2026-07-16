package com.ark.commerce.product

data class ProductQueryFilter(
    val status: ProductStatus? = null,
    val name: String? = null,
    val minPrice: Long? = null,
    val maxPrice: Long? = null,
    val category: String? = null,
)
