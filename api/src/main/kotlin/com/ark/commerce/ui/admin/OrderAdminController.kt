package com.ark.commerce.ui.admin

import com.ark.commerce.application.OrderService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin/orders")
class OrderAdminController(
    private val orderService: OrderService,
) {
    @GetMapping
    fun list(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): String {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        model.addAttribute("orders", orderService.listOrders(pageable))
        return "commerce/admin/orders"
    }

    @PostMapping("/{id}/confirm")
    fun confirm(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ) = transition(redirectAttributes) { orderService.confirm(id) }

    @PostMapping("/{id}/ship")
    fun ship(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ) = transition(redirectAttributes) { orderService.ship(id) }

    @PostMapping("/{id}/deliver")
    fun deliver(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ) = transition(redirectAttributes) { orderService.deliver(id) }

    @PostMapping("/{id}/cancel")
    fun cancel(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ) = transition(redirectAttributes) { orderService.cancel(id) }

    private fun transition(
        redirectAttributes: RedirectAttributes,
        action: () -> Unit,
    ): String {
        runCatching(action)
            .onSuccess { redirectAttributes.addFlashAttribute("success", "상태가 변경됐습니다.") }
            .onFailure { redirectAttributes.addFlashAttribute("error", it.message ?: "오류가 발생했습니다.") }
        return "redirect:/admin/orders"
    }
}
