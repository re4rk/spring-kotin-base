package com.ark.base.application

import com.ark.base.auth.oauth.OAuthHttpClient
import com.ark.base.auth.oauth.OAuthProvider
import com.ark.base.auth.oauth.OAuthState
import com.ark.base.auth.oauth.OAuthStateRepository
import com.ark.base.auth.oauth.OAuthUserInfo
import com.ark.base.auth.oauth.UserOAuthAccount
import com.ark.base.auth.oauth.UserOAuthAccountRepository
import com.ark.base.auth.refreshToken.RefreshTokenRepository
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.common.JwtProvider
import com.ark.base.user.User
import com.ark.base.user.UserRepository
import com.ark.base.user.UserRole
import com.ark.base.user.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class OAuthService(
    private val userRepository: UserRepository,
    private val userOAuthAccountRepository: UserOAuthAccountRepository,
    private val oAuthStateRepository: OAuthStateRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProvider: JwtProvider,
    oAuthHttpClients: List<OAuthHttpClient>,
) {
    private val oAuthHttpClientsByProvider = oAuthHttpClients.associateBy { it.provider }

    fun getAuthorizationUrl(
        provider: String,
        redirectUri: String,
        linkUserId: Long? = null,
    ): OAuthAuthorizationResponse {
        val oauthProvider = parseProvider(provider)
        val oAuthHttpClient =
            oAuthHttpClientsByProvider[oauthProvider]
                ?: throw BaseException(ErrorCode.OAUTH_UNSUPPORTED_PROVIDER)
        val state = UUID.randomUUID().toString()
        oAuthStateRepository.save(
            OAuthState(
                state = state,
                provider = provider,
                redirectUri = redirectUri,
                linkUserId = linkUserId,
            ),
        )
        return OAuthAuthorizationResponse(
            state = state,
            authorizationUrl = oAuthHttpClient.buildAuthorizationUrl(state, redirectUri),
        )
    }

    @Transactional(readOnly = true)
    fun listLinkedAccounts(userId: Long): List<UserOAuthAccount> = userOAuthAccountRepository.findAllByUserId(userId)

    @Transactional
    fun authenticate(
        provider: String,
        request: OAuthCallbackRequest,
    ): User {
        val userInfo = exchangeCode(provider, request, expectLinkUserId = null)
        return findOrCreateUser(userInfo)
    }

    @Transactional
    fun linkAccount(
        provider: String,
        request: OAuthCallbackRequest,
        userId: Long,
    ): UserOAuthAccount {
        val userInfo = exchangeCode(provider, request, expectLinkUserId = userId)

        userOAuthAccountRepository.findByUserIdAndProvider(userId, userInfo.provider)?.let {
            throw BaseException(ErrorCode.OAUTH_ALREADY_CONNECTED)
        }

        userOAuthAccountRepository
            .findByProviderAndProviderUserId(userInfo.provider, userInfo.providerUserId)
            ?.let { throw BaseException(ErrorCode.OAUTH_ACCOUNT_ALREADY_LINKED) }

        return userOAuthAccountRepository.save(
            UserOAuthAccount(
                userId = userId,
                provider = userInfo.provider,
                providerUserId = userInfo.providerUserId,
                providerEmail = userInfo.email,
            ),
        )
    }

    @Transactional
    fun unlinkAccount(
        userId: Long,
        provider: String,
    ) {
        val oauthProvider = parseProvider(provider)
        val account =
            userOAuthAccountRepository.findByUserIdAndProvider(userId, oauthProvider)
                ?: throw BaseException(ErrorCode.NOT_FOUND)
        userOAuthAccountRepository.delete(account)
    }

    @Transactional
    fun callback(
        provider: String,
        request: OAuthCallbackRequest,
    ): TokenResponse {
        val user = authenticate(provider, request)
        val accessToken = jwtProvider.generate(user.id)
        val refreshToken = refreshTokenRepository.issue(user.id)
        return TokenResponse(accessToken = accessToken, refreshToken = refreshToken.token)
    }

    private fun exchangeCode(
        provider: String,
        request: OAuthCallbackRequest,
        expectLinkUserId: Long?,
    ): OAuthUserInfo {
        val savedState =
            oAuthStateRepository.findById(request.state).orElseThrow {
                BaseException(ErrorCode.OAUTH_INVALID_STATE)
            }
        oAuthStateRepository.deleteById(request.state)

        if (savedState.provider != provider) throw BaseException(ErrorCode.OAUTH_INVALID_STATE)
        if (savedState.linkUserId != expectLinkUserId) throw BaseException(ErrorCode.OAUTH_INVALID_STATE)

        val oauthProvider = parseProvider(provider)
        val oAuthHttpClient =
            oAuthHttpClientsByProvider[oauthProvider]
                ?: throw BaseException(ErrorCode.OAUTH_UNSUPPORTED_PROVIDER)
        return oAuthHttpClient.getUserInfo(request.code, request.redirectUri, request.state)
    }

    private fun findOrCreateUser(userInfo: OAuthUserInfo): User {
        userOAuthAccountRepository
            .findByProviderAndProviderUserId(userInfo.provider, userInfo.providerUserId)
            ?.let { return userRepository.findByIdOrThrow(it.userId) }

        val email = userInfo.email ?: throw BaseException(ErrorCode.OAUTH_EMAIL_REQUIRED)
        val isFirstUser = userRepository.count() == 0L
        val user =
            userRepository.findByEmail(email) ?: run {
                val created = User(email = email, name = userInfo.name)
                if (isFirstUser) created.role = UserRole.ADMIN
                userRepository.save(created)
            }

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

    private fun parseProvider(provider: String): OAuthProvider =
        runCatching { OAuthProvider.valueOf(provider.uppercase()) }.getOrElse {
            throw BaseException(ErrorCode.OAUTH_UNSUPPORTED_PROVIDER)
        }
}
