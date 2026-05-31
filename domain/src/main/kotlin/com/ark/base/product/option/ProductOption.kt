package com.ark.base.product.option

import com.ark.base.common.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class ProductOption(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_group_id")
    val optionGroup: ProductOptionGroup,
    var name: String,
    var sortOrder: Int = 0,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity()
