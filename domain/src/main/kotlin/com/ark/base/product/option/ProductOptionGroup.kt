package com.ark.base.product.option

import com.ark.base.common.BaseEntity
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy

@Entity
class ProductOptionGroup(
    var name: String,
    var sortOrder: Int = 0,
    @OneToMany(mappedBy = "optionGroup", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    val options: MutableList<ProductOption> = mutableListOf(),
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    fun addOption(option: ProductOption) {
        options.add(option)
    }

    fun removeOption(optionId: Long) {
        val option = options.find { it.id == optionId } ?: throw BaseException(ErrorCode.PRODUCT_OPTION_NOT_FOUND)
        options.remove(option)
    }
}
