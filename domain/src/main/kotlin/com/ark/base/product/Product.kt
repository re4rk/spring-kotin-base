package com.ark.base.product

import com.ark.base.common.BaseAggregateEntity
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.product.option.ProductOptionGroup
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy

@Entity
class Product(
    var name: String,
    var price: Long,
    var description: String? = null,
    var category: String? = null,
    var thumbnailUrl: String? = null,
    @Enumerated(EnumType.STRING)
    var status: ProductStatus = ProductStatus.DRAFT,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "product_id")
    @OrderBy("sortOrder ASC")
    val optionGroups: MutableList<ProductOptionGroup> = mutableListOf(),
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseAggregateEntity<Product>() {
    val isOrderable: Boolean get() = status.isOrderable

    fun addOptionGroup(group: ProductOptionGroup) {
        optionGroups.add(group)
    }

    fun removeOptionGroup(groupId: Long) {
        val group = optionGroups.find { it.id == groupId }
            ?: throw BaseException(ErrorCode.PRODUCT_OPTION_GROUP_NOT_FOUND)
        optionGroups.remove(group)
    }

    fun update(
        name: String,
        price: Long,
        description: String?,
        category: String?,
        thumbnailUrl: String?,
    ) {
        this.name = name
        this.price = price
        this.description = description
        this.category = category
        this.thumbnailUrl = thumbnailUrl
    }

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
