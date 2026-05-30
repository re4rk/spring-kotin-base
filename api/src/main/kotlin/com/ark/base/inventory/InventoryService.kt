package com.ark.base.inventory

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
