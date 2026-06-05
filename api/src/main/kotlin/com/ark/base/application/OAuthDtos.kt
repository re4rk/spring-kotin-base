package com.ark.base.application

data class OAuthCallbackRequest(
    val code: String,
    val state: String,
    val redirectUri: String,
)

data class OAuthAuthorizationResponse(
    val state: String,
    val authorizationUrl: String,
)
