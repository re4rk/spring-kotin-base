package com.ark.base.user

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    val email: String,
    val name: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
)
