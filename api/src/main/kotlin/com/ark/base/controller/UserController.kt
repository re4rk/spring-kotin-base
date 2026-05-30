package com.ark.base.controller

import com.ark.base.application.ChangePasswordRequest
import com.ark.base.application.RegisterRequest
import com.ark.base.application.UserResponse
import com.ark.base.application.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping
    fun findByEmail(
        @RequestParam email: String,
    ): UserResponse? = userService.findByEmail(email)?.let { UserResponse.from(it) }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @RequestBody request: RegisterRequest,
    ): UserResponse = userService.register(request)

    @PatchMapping("/{userId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changePassword(
        @PathVariable userId: Long,
        @RequestBody request: ChangePasswordRequest,
    ) = userService.changePassword(userId, request)

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable userId: Long,
    ) = userService.delete(userId)
}
