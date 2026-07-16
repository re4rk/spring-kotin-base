package com.ark.commerce.product

sealed class ProductEvent(
    private val product: Product,
) {
    val productId get() = product.id
    val productName get() = product.name
    val price get() = product.price
    val status get() = product.status
}

class ProductCreatedEvent(
    product: Product,
) : ProductEvent(product)

class ProductSubmittedEvent(
    product: Product,
) : ProductEvent(product)

class ProductApprovedEvent(
    product: Product,
) : ProductEvent(product)

class ProductRejectedEvent(
    product: Product,
) : ProductEvent(product)

class ProductMarkedSoldOutEvent(
    product: Product,
) : ProductEvent(product)

class ProductSaleResumedEvent(
    product: Product,
) : ProductEvent(product)

class ProductDiscontinuedEvent(
    product: Product,
) : ProductEvent(product)
