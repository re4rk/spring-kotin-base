package com.ark.base.product

class ProductCreatedEvent(
    private val product: Product,
) {
    val productId get() = product.id
    val productName get() = product.name
    val price get() = product.price
    val status get() = product.status
}

class ProductSubmittedEvent(
    private val product: Product,
) {
    val productId get() = product.id
    val productName get() = product.name
    val price get() = product.price
    val status get() = product.status
}

class ProductApprovedEvent(
    private val product: Product,
) {
    val productId get() = product.id
    val productName get() = product.name
    val price get() = product.price
    val status get() = product.status
}

class ProductRejectedEvent(
    private val product: Product,
) {
    val productId get() = product.id
    val productName get() = product.name
    val price get() = product.price
    val status get() = product.status
}

class ProductMarkedSoldOutEvent(
    private val product: Product,
) {
    val productId get() = product.id
    val productName get() = product.name
    val price get() = product.price
    val status get() = product.status
}

class ProductSaleResumedEvent(
    private val product: Product,
) {
    val productId get() = product.id
    val productName get() = product.name
    val price get() = product.price
    val status get() = product.status
}

class ProductDiscontinuedEvent(
    private val product: Product,
) {
    val productId get() = product.id
    val productName get() = product.name
    val price get() = product.price
    val status get() = product.status
}
