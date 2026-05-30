package com.ark.base.controller

import com.ark.base.application.InventoryDecreaseRequest
import com.ark.base.application.InventoryService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/inventories")
class InventoryController(
    private val inventoryService: InventoryService,
) {
    @PatchMapping("/{inventoryId}/decrease")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun decrease(
        @PathVariable inventoryId: Long,
        @RequestBody request: InventoryDecreaseRequest,
    ) = inventoryService.decrease(inventoryId, request.quantity)
}
