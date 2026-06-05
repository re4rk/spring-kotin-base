package com.ark.base.auth.oauth

import org.springframework.data.jpa.repository.JpaRepository

interface UserOAuthAccountRepository : JpaRepository<UserOAuthAccount, Long> {
    fun findByProviderAndProviderUserId(
        provider: OAuthProvider,
        providerUserId: String,
    ): UserOAuthAccount?

    fun findAllByUserId(userId: Long): List<UserOAuthAccount>
}
