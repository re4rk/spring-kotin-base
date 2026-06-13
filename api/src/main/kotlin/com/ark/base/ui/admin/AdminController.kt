package com.ark.base.ui.admin

import com.ark.base.application.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin")
class AdminController(
    private val userService: UserService,
) {
    @GetMapping("", "/")
    fun index() = "redirect:/admin/users"

    @GetMapping("/users")
    fun users(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): String {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        val users = userService.listUsers(pageable)
        model.addAttribute("users", users)
        return "admin/users"
    }

    @GetMapping("/login")
    fun login(
        @RequestParam(required = false) error: String?,
        @RequestParam(required = false) logout: String?,
        model: Model,
    ): String {
        if (error != null) model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.")
        if (logout != null) model.addAttribute("message", "로그아웃되었습니다.")
        return "admin/login"
    }
}
