package com.ark.base.ui.admin

import com.ark.base.application.UserResponse
import com.ark.base.application.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/api")
class AdminUserController(
    private val userService: UserService,
) {
    @GetMapping("/users")
    fun listUsers(
        @PageableDefault(size = 20, sort = ["id"]) pageable: Pageable,
    ): Page<UserResponse> = userService.listUsers(pageable).map { UserResponse.from(it) }
}
