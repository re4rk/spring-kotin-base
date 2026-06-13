package com.ark.base.ui

import com.ark.base.common.JwtProvider
import com.ark.base.common.SecurityUser
import com.ark.base.user.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = resolveBearerToken(request)
        if (token != null && SecurityContextHolder.getContext().authentication == null) {
            runCatching { jwtProvider.parseUserId(token) }
                .onSuccess { userId ->
                    val user = userRepository.findById(userId).orElse(null) ?: return@onSuccess
                    val securityUser = SecurityUser(userId, user.role)
                    val authentication =
                        UsernamePasswordAuthenticationToken(
                            securityUser,
                            null,
                            securityUser.authorities,
                        ).apply {
                            details = WebAuthenticationDetailsSource().buildDetails(request)
                        }
                    SecurityContextHolder.getContext().authentication = authentication
                }
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveBearerToken(request: HttpServletRequest): String? {
        val header = request.getHeader(AUTHORIZATION_HEADER) ?: return null
        if (!header.startsWith(BEARER_PREFIX)) return null
        return header.removePrefix(BEARER_PREFIX).trim().takeIf { it.isNotEmpty() }
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
}
