package com.ark.base.application

import com.ark.base.inventory.Inventory
import com.ark.base.product.Product
import com.ark.base.product.ProductQueryFilter
import com.ark.base.product.ProductStatus
import com.ark.base.product.option.ProductOption
import com.ark.base.product.option.ProductOptionGroup

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

data class ProductOptionCreateRequest(
    val name: String,
    val extraPrice: Long = 0,
    val stock: Int,
    val sortOrder: Int = 0,
)

data class ProductOptionGroupCreateRequest(
    val name: String,
    val sortOrder: Int = 0,
    val options: List<ProductOptionCreateRequest>,
) {
    fun toOptionGroup(): ProductOptionGroup {
        val group = ProductOptionGroup(name = name, sortOrder = sortOrder)
        options.forEach { group.addOption(ProductOption(optionGroup = group, name = it.name, extraPrice = it.extraPrice, stock = it.stock, sortOrder = it.sortOrder)) }
        return group
    }
}

data class ProductOptionResponse(
    val id: Long,
    val name: String,
    val extraPrice: Long,
    val stock: Int,
    val sortOrder: Int,
) {
    companion object {
        fun from(option: ProductOption) = ProductOptionResponse(
            id = option.id,
            name = option.name,
            extraPrice = option.extraPrice,
            stock = option.stock,
            sortOrder = option.sortOrder,
        )
    }
}

data class ProductOptionGroupResponse(
    val id: Long,
    val name: String,
    val sortOrder: Int,
    val options: List<ProductOptionResponse>,
) {
    companion object {
        fun from(group: ProductOptionGroup) = ProductOptionGroupResponse(
            id = group.id,
            name = group.name,
            sortOrder = group.sortOrder,
            options = group.options.map { ProductOptionResponse.from(it) },
        )
    }
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
    val optionGroups: List<ProductOptionGroupResponse>,
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
            optionGroups = product.optionGroups.map { ProductOptionGroupResponse.from(it) },
        )
    }
}
