package com.ark.commerce.product

import com.ark.base.common.BaseAggregateEntity
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.commerce.product.option.ProductOptionGroup
import com.ark.commerce.product.option.ProductSku
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
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
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    val optionGroups: MutableList<ProductOptionGroup> = mutableListOf(),
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val skus: MutableList<ProductSku> = mutableListOf(),
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseAggregateEntity<Product>() {
    val isOrderable: Boolean get() = status.isOrderable

    fun addOptionGroup(group: ProductOptionGroup) {
        group.product = this
        optionGroups.add(group)
    }

    fun removeOptionGroup(groupId: Long) {
        val group =
            optionGroups.find { it.id == groupId }
                ?: throw BaseException(ErrorCode.PRODUCT_OPTION_GROUP_NOT_FOUND)
        val optionIds = group.options.map { it.id }.toSet()
        if (skus.any { sku -> sku.options.any { it.id in optionIds } }) {
            throw BaseException(ErrorCode.PRODUCT_OPTION_IN_USE)
        }
        optionGroups.remove(group)
    }

    fun addSku(sku: ProductSku) {
        sku.product = this
        skus.add(sku)
    }

    fun removeSku(skuId: Long) {
        skus.remove(findSkuOrThrow(skuId))
    }

    fun findSkuOrThrow(skuId: Long): ProductSku = skus.find { it.id == skuId } ?: throw BaseException(ErrorCode.PRODUCT_SKU_NOT_FOUND)

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
        transitionTo(ProductStatus.DISCONTINUED)
        registerEvent(ProductDiscontinuedEvent(this))
    }

    private fun transitionTo(target: ProductStatus) {
        if (!status.canTransitionTo(target)) throw BaseException(ErrorCode.PRODUCT_INVALID_STATUS)
        status = target
    }
}
