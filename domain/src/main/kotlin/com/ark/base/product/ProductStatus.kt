package com.ark.base.product

enum class ProductStatus {
    DRAFT,
    PENDING,
    ON_SALE,
    SOLD_OUT,
    DISCONTINUED,
    ;

    fun canTransitionTo(target: ProductStatus): Boolean =
        when (this) {
            DRAFT -> target in setOf(PENDING, DISCONTINUED)
            PENDING -> target in setOf(ON_SALE, DRAFT, DISCONTINUED)
            ON_SALE -> target in setOf(SOLD_OUT, DISCONTINUED)
            SOLD_OUT -> target in setOf(ON_SALE, DISCONTINUED)
            DISCONTINUED -> false
        }

    val isOrderable: Boolean get() = this == ON_SALE
}
