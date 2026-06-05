package com.ark.base.infra.oauth

import com.ark.base.auth.oauth.OAuthProvider

data class OAuthUserInfo(
    val provider: OAuthProvider,
    val providerUserId: String,
    val email: String?,
    val name: String,
)
