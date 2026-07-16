package com.ark.clients.oauth

import com.ark.base.auth.oauth.OAuthHttpClient
import com.ark.base.auth.oauth.OAuthProvider
import com.ark.base.auth.oauth.OAuthUserInfo
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.clients.config.OAuthProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder

@Component
class GoogleOAuthHttpClient(
    private val oauthProperties: OAuthProperties,
) : OAuthHttpClient {
    private val log = LoggerFactory.getLogger(javaClass)
    private val restClient = RestClient.create()

    override val provider = OAuthProvider.GOOGLE

    override fun buildAuthorizationUrl(
        state: String,
        redirectUri: String,
    ): String =
        UriComponentsBuilder
            .fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
            .queryParam("client_id", oauthProperties.google.clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("response_type", "code")
            .queryParam("scope", "openid email profile")
            .queryParam("state", state)
            .queryParam("access_type", "offline")
            .build()
            .toUriString()

    override fun getUserInfo(
        code: String,
        redirectUri: String,
        state: String?,
    ): OAuthUserInfo {
        val accessToken = exchangeCodeForToken(code, redirectUri)
        return fetchUserInfo(accessToken)
    }

    private fun exchangeCodeForToken(
        code: String,
        redirectUri: String,
    ): String {
        val props = oauthProperties.google
        val body =
            LinkedMultiValueMap<String, String>().apply {
                add("grant_type", "authorization_code")
                add("code", code)
                add("redirect_uri", redirectUri)
                add("client_id", props.clientId)
                add("client_secret", props.clientSecret)
            }
        return runCatching {
            restClient
                .post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(GoogleTokenResponse::class.java)!!
                .accessToken
        }.getOrElse { ex ->
            log.error("Google token exchange failed error={}", ex.message, ex)
            throw BaseException(ErrorCode.OAUTH_PROVIDER_ERROR)
        }
    }

    private fun fetchUserInfo(accessToken: String): OAuthUserInfo =
        runCatching {
            val response =
                restClient
                    .get()
                    .uri(USER_INFO_URL)
                    .header("Authorization", "Bearer $accessToken")
                    .retrieve()
                    .body(GoogleUserInfoResponse::class.java)!!
            OAuthUserInfo(
                provider = OAuthProvider.GOOGLE,
                providerUserId = response.id,
                email = response.email,
                name = response.name,
            )
        }.getOrElse { ex ->
            log.error("Google user info fetch failed error={}", ex.message, ex)
            throw BaseException(ErrorCode.OAUTH_PROVIDER_ERROR)
        }

    private data class GoogleTokenResponse(
        @JsonProperty("access_token") val accessToken: String,
    )

    private data class GoogleUserInfoResponse(
        val id: String,
        val email: String?,
        val name: String,
    )

    companion object {
        private const val TOKEN_URL = "https://oauth2.googleapis.com/token"
        private const val USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo"
    }
}
