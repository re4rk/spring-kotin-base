package com.ark.base.auth.oauth

interface OAuthHttpClient {
    val provider: OAuthProvider

    fun buildAuthorizationUrl(
        state: String,
        redirectUri: String,
    ): String

    fun getUserInfo(
        code: String,
        redirectUri: String,
        state: String? = null,
    ): OAuthUserInfo
}
