package com.ark.base.inventory

import jakarta.persistence.*

@Entity
class Inventory(
    val productName: String,
    var stock: Int,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
)
