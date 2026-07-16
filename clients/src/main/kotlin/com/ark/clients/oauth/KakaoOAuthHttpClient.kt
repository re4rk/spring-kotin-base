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
class KakaoOAuthHttpClient(
    private val oauthProperties: OAuthProperties,
) : OAuthHttpClient {
    private val log = LoggerFactory.getLogger(javaClass)
    private val restClient = RestClient.create()

    override val provider = OAuthProvider.KAKAO

    override fun buildAuthorizationUrl(
        state: String,
        redirectUri: String,
    ): String =
        UriComponentsBuilder
            .fromUriString("https://kauth.kakao.com/oauth/authorize")
            .queryParam("client_id", oauthProperties.kakao.clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("response_type", "code")
            .queryParam("state", state)
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
        val props = oauthProperties.kakao
        val body =
            LinkedMultiValueMap<String, String>().apply {
                add("grant_type", "authorization_code")
                add("code", code)
                add("redirect_uri", redirectUri)
                add("client_id", props.clientId)
                if (props.clientSecret.isNotBlank()) {
                    add("client_secret", props.clientSecret)
                }
            }
        return runCatching {
            restClient
                .post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(KakaoTokenResponse::class.java)!!
                .accessToken
        }.getOrElse { ex ->
            log.error("Kakao token exchange failed error={}", ex.message, ex)
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
                    .body(KakaoUserInfoResponse::class.java)!!
            OAuthUserInfo(
                provider = OAuthProvider.KAKAO,
                providerUserId = response.id.toString(),
                email = response.kakaoAccount?.email,
                name = response.kakaoAccount?.profile?.nickname ?: response.id.toString(),
            )
        }.getOrElse { ex ->
            log.error("Kakao user info fetch failed error={}", ex.message, ex)
            throw BaseException(ErrorCode.OAUTH_PROVIDER_ERROR)
        }

    private data class KakaoTokenResponse(
        @JsonProperty("access_token") val accessToken: String,
    )

    private data class KakaoUserInfoResponse(
        val id: Long,
        @JsonProperty("kakao_account") val kakaoAccount: KakaoAccount?,
    )

    private data class KakaoAccount(
        val email: String?,
        val profile: KakaoProfile?,
    )

    private data class KakaoProfile(
        val nickname: String?,
    )

    companion object {
        private const val TOKEN_URL = "https://kauth.kakao.com/oauth/token"
        private const val USER_INFO_URL = "https://kapi.kakao.com/v2/user/me"
    }
}
