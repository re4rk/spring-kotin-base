package com.ark.base.auth.oauth

data class OAuthUserInfo(
    val provider: OAuthProvider,
    val providerUserId: String,
    val email: String?,
    val name: String,
)
