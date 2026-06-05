package com.ark.base.auth.oauth

import com.ark.base.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "user_oauth_account")
class UserOAuthAccount(
    @Column(nullable = false)
    val userId: Long,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val provider: OAuthProvider,
    @Column(nullable = false)
    val providerUserId: String,
    val providerEmail: String? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity()
