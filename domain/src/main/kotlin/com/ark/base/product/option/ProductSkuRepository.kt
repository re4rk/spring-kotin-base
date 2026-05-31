package com.ark.base.product.option

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.data.jpa.repository.JpaRepository

interface ProductSkuRepository : JpaRepository<ProductSku, Long>

fun ProductSkuRepository.findByIdOrThrow(id: Long): ProductSku = findById(id).orElseThrow { BaseException(ErrorCode.PRODUCT_SKU_NOT_FOUND) }
