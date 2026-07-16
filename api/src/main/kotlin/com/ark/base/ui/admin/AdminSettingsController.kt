package com.ark.base.ui.admin

import com.ark.base.application.AdminBootstrapService
import com.ark.base.application.AdminSetPasswordRequest
import com.ark.base.application.OAuthCallbackRequest
import com.ark.base.application.OAuthService
import com.ark.base.auth.oauth.OAuthProvider
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.user.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Controller
@RequestMapping("/admin/settings")
class AdminSettingsController(
    private val userRepository: UserRepository,
    private val oauthService: OAuthService,
    private val adminBootstrapService: AdminBootstrapService,
) {
    @GetMapping
    fun settings(
        authentication: Authentication,
        model: Model,
        @RequestParam(required = false) linked: String?,
        @RequestParam(required = false) unlinked: String?,
        @RequestParam(required = false) password: String?,
        @RequestParam(required = false) error: String?,
    ): String {
        val user =
            userRepository.findByEmail(authentication.name)
                ?: throw BaseException(ErrorCode.USER_NOT_FOUND)
        val kakaoAccount =
            oauthService
                .listLinkedAccounts(user.id)
                .firstOrNull { it.provider == OAuthProvider.KAKAO }

        model.addAttribute("user", user)
        model.addAttribute("hasPassword", user.hasPassword())
        model.addAttribute("kakaoLinked", kakaoAccount != null)
        model.addAttribute("kakaoEmail", kakaoAccount?.providerEmail)

        when {
            linked != null -> model.addAttribute("success", "카카오 계정이 연동되었습니다.")
            unlinked != null -> model.addAttribute("success", "카카오 연동이 해제되었습니다.")
            password == "set" -> model.addAttribute("success", "비밀번호가 설정되었습니다.")
            password == "changed" -> model.addAttribute("success", "비밀번호가 변경되었습니다.")
            error == "link" -> model.addAttribute("error", "카카오 계정 연동에 실패했습니다.")
            error == "already_linked" -> model.addAttribute("error", "이미 다른 계정에 연동된 카카오 계정입니다.")
            error == "already_connected" -> model.addAttribute("error", "이미 연동된 카카오 계정입니다.")
            error == "password_short" -> model.addAttribute("error", "비밀번호는 8자 이상이어야 합니다.")
            error == "password_current" -> model.addAttribute("error", "현재 비밀번호가 올바르지 않습니다.")
            error == "password" -> model.addAttribute("error", "비밀번호 변경에 실패했습니다.")
        }

        return "base/admin/settings"
    }

    @PostMapping("/password")
    fun updatePassword(
        authentication: Authentication,
        @RequestParam password: String,
        @RequestParam(required = false) currentPassword: String?,
    ): String {
        val user =
            userRepository.findByEmail(authentication.name)
                ?: throw BaseException(ErrorCode.USER_NOT_FOUND)
        val hadPassword = user.hasPassword()
        return try {
            adminBootstrapService.setPassword(
                userId = user.id,
                request =
                    AdminSetPasswordRequest(
                        password = password,
                        currentPassword = currentPassword,
                    ),
            )
            if (hadPassword) {
                "redirect:/admin/settings?password=changed"
            } else {
                "redirect:/admin/settings?password=set"
            }
        } catch (e: BaseException) {
            when (e.errorCode) {
                ErrorCode.ADMIN_PASSWORD_TOO_SHORT -> "redirect:/admin/settings?error=password_short"
                ErrorCode.USER_LOGIN_FAILED -> "redirect:/admin/settings?error=password_current"
                else -> "redirect:/admin/settings?error=password"
            }
        } catch (e: Exception) {
            "redirect:/admin/settings?error=password"
        }
    }

    @GetMapping("/oauth/kakao/link")
    fun startKakaoLink(
        authentication: Authentication,
        request: HttpServletRequest,
    ): String {
        val user =
            userRepository.findByEmail(authentication.name)
                ?: throw BaseException(ErrorCode.USER_NOT_FOUND)
        val redirectUri = kakaoCallbackUri(request)
        val authorization = oauthService.getAuthorizationUrl("kakao", redirectUri, linkUserId = user.id)
        return "redirect:${authorization.authorizationUrl}"
    }

    @GetMapping("/oauth/kakao/callback")
    fun kakaoLinkCallback(
        @RequestParam code: String,
        @RequestParam state: String,
        authentication: Authentication,
        request: HttpServletRequest,
    ): String {
        val user =
            userRepository.findByEmail(authentication.name)
                ?: return "redirect:/admin/login"
        return try {
            oauthService.linkAccount(
                "kakao",
                OAuthCallbackRequest(
                    code = code,
                    state = state,
                    redirectUri = kakaoCallbackUri(request),
                ),
                userId = user.id,
            )
            "redirect:/admin/settings?linked=kakao"
        } catch (e: BaseException) {
            when (e.errorCode) {
                ErrorCode.OAUTH_ACCOUNT_ALREADY_LINKED ->
                    "redirect:/admin/settings?error=already_linked"
                ErrorCode.OAUTH_ALREADY_CONNECTED ->
                    "redirect:/admin/settings?error=already_connected"
                else -> "redirect:/admin/settings?error=link"
            }
        } catch (e: Exception) {
            "redirect:/admin/settings?error=link"
        }
    }

    @PostMapping("/oauth/kakao/unlink")
    fun unlinkKakao(authentication: Authentication): String {
        val user =
            userRepository.findByEmail(authentication.name)
                ?: throw BaseException(ErrorCode.USER_NOT_FOUND)
        oauthService.unlinkAccount(user.id, "kakao")
        return "redirect:/admin/settings?unlinked=kakao"
    }

    private fun kakaoCallbackUri(request: HttpServletRequest): String =
        ServletUriComponentsBuilder
            .fromRequest(request)
            .replacePath("/admin/settings/oauth/kakao/callback")
            .replaceQuery(null)
            .build()
            .toUriString()
}
