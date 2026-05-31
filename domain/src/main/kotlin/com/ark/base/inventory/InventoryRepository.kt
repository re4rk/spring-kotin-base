package com.ark.base.inventory

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryRepository : JpaRepository<Inventory, Long> {
    fun findByProductId(productId: Long): Inventory?
}

fun InventoryRepository.findByIdOrThrow(id: Long): Inventory = findById(id).orElseThrow { BaseException(ErrorCode.INVENTORY_NOT_FOUND) }

fun InventoryRepository.findByProductIdOrThrow(productId: Long): Inventory =
    findByProductId(productId) ?: throw BaseException(ErrorCode.INVENTORY_NOT_FOUND)
