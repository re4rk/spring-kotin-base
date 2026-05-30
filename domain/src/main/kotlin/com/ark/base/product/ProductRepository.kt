package com.ark.base.product

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>

fun ProductRepository.getById(id: Long): Product = findById(id).orElseThrow { BaseException(ErrorCode.PRODUCT_NOT_FOUND) }
