package com.ark.base.inventory

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryRepository : JpaRepository<Inventory, Long> {
    companion object {
        fun InventoryRepository.getById(id: Long): Inventory =
            findById(id).orElseThrow { BaseException(ErrorCode.INVENTORY_NOT_FOUND) }

    }
}
