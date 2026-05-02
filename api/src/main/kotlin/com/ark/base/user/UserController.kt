package com.ark.base.user

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userApiService: UserApiService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @RequestBody request: RegisterRequest,
    ): UserResponse = userApiService.register(request)

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
    ): TokenResponse = userApiService.login(request)

    @PatchMapping("/{userId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changePassword(
        @PathVariable userId: Long,
        @RequestBody request: ChangePasswordRequest,
    ) = userApiService.changePassword(userId, request)
}
