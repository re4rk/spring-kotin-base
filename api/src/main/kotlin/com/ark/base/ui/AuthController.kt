package com.ark.base.ui

import com.ark.base.application.AuthService
import com.ark.base.application.LoginRequest
import com.ark.base.application.PasswordResetConfirmRequest
import com.ark.base.application.PasswordResetRequest
import com.ark.base.application.RegisterRequest
import com.ark.base.application.TokenResponse
import com.ark.base.application.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @RequestBody request: RegisterRequest,
    ): UserResponse = authService.register(request)

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
    ): TokenResponse = authService.login(request)

    @PostMapping("/passwords/reset/init")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun requestPasswordReset(
        @RequestBody request: PasswordResetRequest,
    ) = authService.requestPasswordReset(request)

    @PostMapping("/passwords/reset/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun resetPassword(
        @RequestBody request: PasswordResetConfirmRequest,
    ) = authService.resetPassword(request)
}
