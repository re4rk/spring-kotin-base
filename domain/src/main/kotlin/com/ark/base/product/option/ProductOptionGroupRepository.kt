package com.ark.base.product.option

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.data.jpa.repository.JpaRepository

interface ProductOptionGroupRepository : JpaRepository<ProductOptionGroup, Long>

fun ProductOptionGroupRepository.findByIdOrThrow(id: Long): ProductOptionGroup =
    findById(id).orElseThrow { BaseException(ErrorCode.PRODUCT_OPTION_GROUP_NOT_FOUND) }
