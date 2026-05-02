package com.ark.base.order

import jakarta.persistence.*

@Entity
@Table(name = "orders")
class Order(
    val userId: Long,
    val productName: String,
    val quantity: Int,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
)
