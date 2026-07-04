package com.ark.base.application

import com.ark.base.auth.oauth.OAuthProvider
import com.ark.base.auth.oauth.OAuthState
import com.ark.base.auth.oauth.OAuthStateRepository
import com.ark.base.auth.oauth.UserOAuthAccount
import com.ark.base.auth.oauth.UserOAuthAccountRepository
import com.ark.base.auth.refreshToken.RefreshTokenRepository
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.common.JwtProvider
import com.ark.base.config.OAuthProperties
import com.ark.base.infra.oauth.GoogleOAuthHttpClient
import com.ark.base.infra.oauth.KakaoOAuthHttpClient
import com.ark.base.infra.oauth.NaverOAuthHttpClient
import com.ark.base.infra.oauth.OAuthUserInfo
import com.ark.base.user.User
import com.ark.base.user.UserRepository
import com.ark.base.user.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@Service
class OAuthService(
    private val userRepository: UserRepository,
    private val userOAuthAccountRepository: UserOAuthAccountRepository,
    private val oAuthStateRepository: OAuthStateRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProvider: JwtProvider,
    private val googleOAuthHttpClient: GoogleOAuthHttpClient,
    private val kakaoOAuthHttpClient: KakaoOAuthHttpClient,
    private val naverOAuthHttpClient: NaverOAuthHttpClient,
    private val oauthProperties: OAuthProperties,
) {
    fun getAuthorizationUrl(
        provider: String,
        redirectUri: String,
    ): OAuthAuthorizationResponse {
        val oauthProvider = parseProvider(provider)
        val state = UUID.randomUUID().toString()
        oAuthStateRepository.save(OAuthState(state = state, provider = provider, redirectUri = redirectUri))
        return OAuthAuthorizationResponse(
            state = state,
            authorizationUrl = buildAuthorizationUrl(oauthProvider, state, redirectUri),
        )
    }

    @Transactional
    fun callback(
        provider: String,
        request: OAuthCallbackRequest,
    ): TokenResponse {
        val savedState =
            oAuthStateRepository.findById(request.state).orElseThrow {
                BaseException(ErrorCode.OAUTH_INVALID_STATE)
            }
        oAuthStateRepository.deleteById(request.state)

        if (savedState.provider != provider) throw BaseException(ErrorCode.OAUTH_INVALID_STATE)

        val oauthProvider = parseProvider(provider)
        val userInfo =
            when (oauthProvider) {
                OAuthProvider.GOOGLE -> googleOAuthHttpClient.getUserInfo(request.code, request.redirectUri)
                OAuthProvider.KAKAO -> kakaoOAuthHttpClient.getUserInfo(request.code, request.redirectUri)
                OAuthProvider.NAVER -> naverOAuthHttpClient.getUserInfo(request.code, request.redirectUri, request.state)
            }

        val user = findOrCreateUser(userInfo)
        val accessToken = jwtProvider.generate(user.id)
        val refreshToken = refreshTokenRepository.issue(user.id)
        return TokenResponse(accessToken = accessToken, refreshToken = refreshToken.token, user = UserResponse.from(user))
    }

    private fun findOrCreateUser(userInfo: OAuthUserInfo): User {
        userOAuthAccountRepository
            .findByProviderAndProviderUserId(userInfo.provider, userInfo.providerUserId)
            ?.let { return userRepository.findByIdOrThrow(it.userId) }

        val email = userInfo.email ?: throw BaseException(ErrorCode.OAUTH_EMAIL_REQUIRED)
        val user =
            userRepository.findByEmail(email)
                ?: userRepository.save(User(email = email, name = userInfo.name))

        userOAuthAccountRepository.save(
            UserOAuthAccount(
                userId = user.id,
                provider = userInfo.provider,
                providerUserId = userInfo.providerUserId,
                providerEmail = userInfo.email,
            ),
        )
        return user
    }

    private fun buildAuthorizationUrl(
        provider: OAuthProvider,
        state: String,
        redirectUri: String,
    ): String =
        when (provider) {
            OAuthProvider.GOOGLE ->
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

            OAuthProvider.KAKAO ->
                UriComponentsBuilder
                    .fromUriString("https://kauth.kakao.com/oauth/authorize")
                    .queryParam("client_id", oauthProperties.kakao.clientId)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("response_type", "code")
                    .queryParam("state", state)
                    .build()
                    .toUriString()

            OAuthProvider.NAVER ->
                UriComponentsBuilder
                    .fromUriString("https://nid.naver.com/oauth2.0/authorize")
                    .queryParam("client_id", oauthProperties.naver.clientId)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("response_type", "code")
                    .queryParam("state", state)
                    .build()
                    .toUriString()
        }

    private fun parseProvider(provider: String): OAuthProvider =
        runCatching { OAuthProvider.valueOf(provider.uppercase()) }.getOrElse {
            throw BaseException(ErrorCode.OAUTH_UNSUPPORTED_PROVIDER)
        }
}
