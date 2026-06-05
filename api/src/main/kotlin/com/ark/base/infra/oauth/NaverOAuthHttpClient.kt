package com.ark.base.infra.oauth

import com.ark.base.auth.oauth.OAuthProvider
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.config.OAuthProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Component
class NaverOAuthHttpClient(
    private val oauthProperties: OAuthProperties,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val restClient = RestClient.create()

    fun getUserInfo(
        code: String,
        redirectUri: String,
        state: String,
    ): OAuthUserInfo {
        val accessToken = exchangeCodeForToken(code, redirectUri, state)
        return fetchUserInfo(accessToken)
    }

    private fun exchangeCodeForToken(
        code: String,
        redirectUri: String,
        state: String,
    ): String {
        val props = oauthProperties.naver
        val body =
            LinkedMultiValueMap<String, String>().apply {
                add("grant_type", "authorization_code")
                add("client_id", props.clientId)
                add("client_secret", props.clientSecret)
                add("redirect_uri", redirectUri)
                add("code", code)
                add("state", state)
            }
        return runCatching {
            restClient
                .post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(NaverTokenResponse::class.java)!!
                .accessToken
        }.getOrElse { ex ->
            log.error("Naver token exchange failed error={}", ex.message, ex)
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
                    .body(NaverUserInfoResponse::class.java)!!
            OAuthUserInfo(
                provider = OAuthProvider.NAVER,
                providerUserId = response.naverUser.id,
                email = response.naverUser.email,
                name = response.naverUser.name ?: response.naverUser.nickname ?: response.naverUser.id,
            )
        }.getOrElse { ex ->
            log.error("Naver user info fetch failed error={}", ex.message, ex)
            throw BaseException(ErrorCode.OAUTH_PROVIDER_ERROR)
        }

    private data class NaverTokenResponse(
        @JsonProperty("access_token") val accessToken: String,
    )

    private data class NaverUserInfoResponse(
        @JsonProperty("response") val naverUser: NaverUser,
    )

    private data class NaverUser(
        val id: String,
        val email: String?,
        val name: String?,
        val nickname: String?,
    )

    companion object {
        private const val TOKEN_URL = "https://nid.naver.com/oauth2.0/token"
        private const val USER_INFO_URL = "https://openapi.naver.com/v1/nid/me"
    }
}
