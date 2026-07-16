package com.ark.base.ui.admin

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.stereotype.Component

@Component
class AdminSessionLogin(
    private val userDetailsService: UserDetailsService,
) {
    private val securityContextRepository: SecurityContextRepository = HttpSessionSecurityContextRepository()

    fun establish(
        email: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        val userDetails = userDetailsService.loadUserByUsername(email)
        val authentication =
            UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        SecurityContextHolder.setContext(context)
        securityContextRepository.saveContext(context, request, response)
    }
}
