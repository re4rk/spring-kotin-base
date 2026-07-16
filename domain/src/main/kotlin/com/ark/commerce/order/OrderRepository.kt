package com.ark.commerce.order

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long>

fun OrderRepository.findByIdOrThrow(id: Long): Order = findById(id).orElseThrow { BaseException(ErrorCode.ORDER_NOT_FOUND) }
