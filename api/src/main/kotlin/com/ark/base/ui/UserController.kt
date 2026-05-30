package com.ark.base.ui

import com.ark.base.application.ChangePasswordRequest
import com.ark.base.application.UserResponse
import com.ark.base.application.UserService
import com.ark.base.common.CurrentUser
import com.ark.base.ui.auth.AccessType
import com.ark.base.ui.auth.Authorize
import com.ark.base.ui.auth.InjectCurrentUser
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
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
    @Authorize(AccessType.SELF_BY_EMAIL, param = "email")
    @GetMapping
    fun findByEmail(
        @RequestParam email: String,
    ): UserResponse? = userService.findByEmail(email)?.let { UserResponse.from(it) }

    @Authorize(AccessType.SELF, param = "userId")
    @PatchMapping("/{userId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changePassword(
        @PathVariable userId: Long,
        @RequestBody request: ChangePasswordRequest,
    ) = userService.changePassword(userId, request)

    @Authorize(AccessType.SELF, param = "userId")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable userId: Long,
        @InjectCurrentUser deletedBy: CurrentUser,
    ) = userService.delete(userId, deletedBy.requireUserId().toString())
}
