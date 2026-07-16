package com.ark.base.ui.admin

import com.ark.base.application.OAuthCallbackRequest
import com.ark.base.application.OAuthService
import com.ark.base.common.BaseException
import com.ark.base.user.UserRole
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Controller
@RequestMapping("/admin/oauth")
class AdminOAuthController(
    private val oauthService: OAuthService,
    private val userDetailsService: UserDetailsService,
) {
    private val securityContextRepository: SecurityContextRepository = HttpSessionSecurityContextRepository()

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

            val userDetails = userDetailsService.loadUserByUsername(user.email)
            val authentication =
                UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            val context = SecurityContextHolder.createEmptyContext()
            context.authentication = authentication
            SecurityContextHolder.setContext(context)
            securityContextRepository.saveContext(context, request, response)

            "redirect:/admin/users"
        } catch (e: BaseException) {
            "redirect:/admin/login?error=oauth"
        } catch (e: Exception) {
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
