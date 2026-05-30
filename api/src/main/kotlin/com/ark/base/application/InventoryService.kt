package com.ark.base.application

import com.ark.base.inventory.InventoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventoryService(
    private val inventoryRepository: InventoryRepository,
) {
    @Transactional
    fun decrease(
        inventoryId: Long,
        quantity: Int,
    ) {
        val inventory = inventoryRepository.getById(inventoryId)
        inventory.decrease(quantity)
    }
}
