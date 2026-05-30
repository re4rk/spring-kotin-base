package com.ark.base.product

import com.ark.base.common.BaseAggregateEntity
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Product(
    val name: String,
    val price: Long,
    @Enumerated(EnumType.STRING)
    var status: ProductStatus = ProductStatus.DRAFT,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseAggregateEntity<Product>() {
    init {
        registerEvent(ProductCreatedEvent(this))
    }

    fun submit() {
        transitionTo(ProductStatus.PENDING)
        registerEvent(ProductSubmittedEvent(this))
    }

    fun approve() {
        transitionTo(ProductStatus.ON_SALE)
        registerEvent(ProductApprovedEvent(this))
    }

    fun reject() {
        transitionTo(ProductStatus.DRAFT)
        registerEvent(ProductRejectedEvent(this))
    }

    fun markSoldOut() {
        transitionTo(ProductStatus.SOLD_OUT)
        registerEvent(ProductMarkedSoldOutEvent(this))
    }

    fun resumeSale() {
        transitionTo(ProductStatus.ON_SALE)
        registerEvent(ProductSaleResumedEvent(this))
    }

    fun discontinue() {
        if (status == ProductStatus.DISCONTINUED) throw BaseException(ErrorCode.PRODUCT_ALREADY_DISCONTINUED)
        status = ProductStatus.DISCONTINUED
        registerEvent(ProductDiscontinuedEvent(this))
    }

    private fun transitionTo(target: ProductStatus) {
        if (!status.canTransitionTo(target)) throw BaseException(ErrorCode.PRODUCT_INVALID_STATUS)
        status = target
    }
}
