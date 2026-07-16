package com.ark.base.ui.admin

import com.ark.base.application.AdminBootstrapService
import com.ark.base.application.CreateInitialAdminRequest
import com.ark.base.application.UserService
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin")
class AdminController(
    private val userService: UserService,
    private val adminBootstrapService: AdminBootstrapService,
    private val adminSessionLogin: AdminSessionLogin,
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
        return "base/admin/users"
    }

    @GetMapping("/login")
    fun login(
        @RequestParam(required = false) error: String?,
        @RequestParam(required = false) logout: String?,
        model: Model,
    ): String {
        val needsSetup = adminBootstrapService.needsSetup()
        model.addAttribute("needsSetup", needsSetup)

        when (error) {
            "oauth_forbidden" -> model.addAttribute("error", "관리자 권한이 없는 카카오 계정입니다.")
            "oauth" -> model.addAttribute("error", "카카오 로그인에 실패했습니다.")
            "setup_done" -> model.addAttribute("error", "이미 초기 설정이 완료되었습니다.")
            "password_short" -> model.addAttribute("error", "비밀번호는 8자 이상이어야 합니다.")
            "duplicate_email" -> model.addAttribute("error", "이미 등록된 이메일입니다.")
            "setup" -> model.addAttribute("error", "초기 설정에 실패했습니다.")
            null -> Unit
            else ->
                if (!needsSetup) {
                    model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.")
                }
        }
        if (logout != null) model.addAttribute("message", "로그아웃되었습니다.")
        return "base/admin/login"
    }

    @PostMapping("/setup")
    fun setup(
        @RequestParam email: String,
        @RequestParam password: String,
        @RequestParam(required = false, defaultValue = "관리자") name: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): String {
        if (!adminBootstrapService.needsSetup()) {
            return "redirect:/admin/login?error=setup_done"
        }
        return try {
            val user =
                adminBootstrapService.createInitialAdmin(
                    CreateInitialAdminRequest(
                        email = email,
                        password = password,
                        name = name,
                    ),
                )
            adminSessionLogin.establish(user.email, request, response)
            "redirect:/admin/users"
        } catch (e: BaseException) {
            when (e.errorCode) {
                ErrorCode.ADMIN_SETUP_ALREADY_DONE -> "redirect:/admin/login?error=setup_done"
                ErrorCode.ADMIN_PASSWORD_TOO_SHORT -> "redirect:/admin/login?error=password_short"
                ErrorCode.USER_DUPLICATE_EMAIL -> "redirect:/admin/login?error=duplicate_email"
                ErrorCode.USER_INVALID_EMAIL,
                ErrorCode.USER_BLANK_NAME,
                ErrorCode.USER_NAME_TOO_LONG,
                -> "redirect:/admin/login?error=setup"
                else -> "redirect:/admin/login?error=setup"
            }
        } catch (e: Exception) {
            "redirect:/admin/login?error=setup"
        }
    }
}
