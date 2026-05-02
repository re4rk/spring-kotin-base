package com.ark.base.user

import com.ark.base.common.JwtProvider
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val jwtProvider: JwtProvider,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @RequestBody request: RegisterRequest,
    ): UserResponse =
        UserResponse.from(
            userService.register(UserRegisterCommand(email = request.email, name = request.name, rawPassword = request.password)),
        )

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
    ): TokenResponse {
        val user = userService.login(UserLoginCommand(email = request.email, rawPassword = request.password))
        return TokenResponse(accessToken = jwtProvider.generate(user.id), user = UserResponse.from(user))
    }
}
