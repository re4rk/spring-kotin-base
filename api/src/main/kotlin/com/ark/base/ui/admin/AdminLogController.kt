package com.ark.base.ui.admin

import com.ark.base.notification.email.EmailLogRepository
import com.ark.base.notification.kakao.KakaoLogRepository
import com.ark.base.notification.push.PushLogRepository
import com.ark.base.notification.slack.SlackLogRepository
import com.ark.base.notification.sms.SmsLogRepository
import com.ark.base.notification.sse.SseLogRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin/logs")
class AdminLogController(
    private val emailLogRepository: EmailLogRepository,
    private val smsLogRepository: SmsLogRepository,
    private val slackLogRepository: SlackLogRepository,
    private val kakaoLogRepository: KakaoLogRepository,
    private val pushLogRepository: PushLogRepository,
    private val sseLogRepository: SseLogRepository,
) {
    @GetMapping("/email")
    fun emailLogs(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): String {
        model.addAttribute("logs", emailLogRepository.findAll(pageable(page, size)))
        return "base/admin/logs/email"
    }

    @GetMapping("/sms")
    fun smsLogs(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): String {
        model.addAttribute("logs", smsLogRepository.findAll(pageable(page, size)))
        return "base/admin/logs/sms"
    }

    @GetMapping("/slack")
    fun slackLogs(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): String {
        model.addAttribute("logs", slackLogRepository.findAll(pageable(page, size)))
        return "base/admin/logs/slack"
    }

    @GetMapping("/kakao")
    fun kakaoLogs(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): String {
        model.addAttribute("logs", kakaoLogRepository.findAll(pageable(page, size)))
        return "base/admin/logs/kakao"
    }

    @GetMapping("/push")
    fun pushLogs(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): String {
        model.addAttribute("logs", pushLogRepository.findAll(pageable(page, size)))
        return "base/admin/logs/push"
    }

    @GetMapping("/sse")
    fun sseLogs(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): String {
        model.addAttribute("logs", sseLogRepository.findAll(pageable(page, size)))
        return "base/admin/logs/sse"
    }

    private fun pageable(
        page: Int,
        size: Int,
    ) = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
}
