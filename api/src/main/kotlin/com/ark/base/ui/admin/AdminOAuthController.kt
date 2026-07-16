package com.ark.base.ui.admin

import com.ark.base.application.OAuthCallbackRequest
import com.ark.base.application.OAuthService
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.user.UserRole
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Controller
@RequestMapping("/admin/oauth")
class AdminOAuthController(
    private val oauthService: OAuthService,
    private val adminSessionLogin: AdminSessionLogin,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/kakao")
    fun kakaoLogin(request: HttpServletRequest): String {
        val redirectUri = callbackUri(request)
        val authorization = oauthService.getAuthorizationUrl("kakao", redirectUri)
        return "redirect:${authorization.authorizationUrl}"
    }

    @GetMapping("/kakao/callback")
    fun kakaoCallback(
        @RequestParam code: String,
        @RequestParam state: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): String {
        return try {
            val user =
                oauthService.authenticate(
                    "kakao",
                    OAuthCallbackRequest(
                        code = code,
                        state = state,
                        redirectUri = callbackUri(request),
                    ),
                )
            if (user.role != UserRole.ADMIN) {
                return "redirect:/admin/login?error=oauth_forbidden"
            }

            adminSessionLogin.establish(user.email, request, response)
            "redirect:/admin/users"
        } catch (e: BaseException) {
            log.warn(
                "Admin Kakao login failed code={} message={}",
                e.errorCode.name,
                e.errorCode.message,
                e,
            )
            when (e.errorCode) {
                ErrorCode.OAUTH_PROVIDER_ERROR -> "redirect:/admin/login?error=oauth_provider"
                ErrorCode.OAUTH_INVALID_STATE -> "redirect:/admin/login?error=oauth_state"
                ErrorCode.OAUTH_EMAIL_REQUIRED -> "redirect:/admin/login?error=oauth_email"
                else -> "redirect:/admin/login?error=oauth"
            }
        } catch (e: Exception) {
            log.error("Admin Kakao login failed", e)
            "redirect:/admin/login?error=oauth"
        }
    }

    private fun callbackUri(request: HttpServletRequest): String =
        ServletUriComponentsBuilder
            .fromRequest(request)
            .replacePath("/admin/oauth/kakao/callback")
            .replaceQuery(null)
            .build()
            .toUriString()
}
