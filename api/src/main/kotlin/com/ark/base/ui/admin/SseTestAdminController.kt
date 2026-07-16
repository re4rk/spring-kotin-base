package com.ark.base.ui.admin

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.common.JwtProvider
import com.ark.base.notification.sse.SseClient
import com.ark.base.user.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/admin/sse-test")
class SseTestAdminController(
    private val sseClient: SseClient,
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
) {
    @GetMapping
    fun page(
        authentication: Authentication,
        model: Model,
    ): String {
        val user =
            userRepository.findByEmail(authentication.name)
                ?: throw BaseException(ErrorCode.USER_NOT_FOUND)
        model.addAttribute("userId", user.id)
        model.addAttribute("userEmail", user.email)
        model.addAttribute("accessToken", jwtProvider.generate(user.id))
        return "base/admin/sse-test"
    }

    @PostMapping("/send")
    @ResponseBody
    fun send(
        authentication: Authentication,
        @RequestParam(required = false) userId: Long?,
        @RequestParam(defaultValue = "test") eventType: String,
        @RequestParam(defaultValue = "ping") data: String,
    ): ResponseEntity<Map<String, Any>> {
        val self =
            userRepository.findByEmail(authentication.name)
                ?: throw BaseException(ErrorCode.USER_NOT_FOUND)
        val targetUserId = userId ?: self.id
        val trimmedType = eventType.trim().ifBlank { "test" }
        val trimmedData = data.trim().ifBlank { "ping" }

        sseClient.send(
            userId = targetUserId,
            eventType = trimmedType,
            data = trimmedData,
        )

        return ResponseEntity.ok(
            mapOf(
                "ok" to true,
                "userId" to targetUserId,
                "eventType" to trimmedType,
                "data" to trimmedData,
            ),
        )
    }
}
