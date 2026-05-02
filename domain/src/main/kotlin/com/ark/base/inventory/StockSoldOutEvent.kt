package com.ark.base.inventory

data class StockSoldOutEvent(
    val inventoryId: Long,
    val productName: String,
)
