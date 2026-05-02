package com.ark.base.inventory

import com.ark.base.common.ErrorCode
import com.ark.base.common.getById
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventoryService(
    private val inventoryRepository: InventoryRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun decrease(inventoryId: Long, quantity: Int) {
        val inventory = inventoryRepository.getById(inventoryId, ErrorCode.INVENTORY_NOT_FOUND)

        inventory.stock -= quantity

        if (inventory.stock <= 0) {
            eventPublisher.publishEvent(StockSoldOutEvent(inventory.id, inventory.productName))
        }
    }
}
