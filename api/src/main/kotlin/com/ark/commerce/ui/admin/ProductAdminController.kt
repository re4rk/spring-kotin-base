package com.ark.commerce.ui.admin

import com.ark.commerce.application.ProductQueryFilterRequest
import com.ark.commerce.application.ProductService
import com.ark.commerce.product.ProductStatus
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
@RequestMapping("/admin/products")
class ProductAdminController(
    private val productService: ProductService,
) {
    @GetMapping
    fun list(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) status: ProductStatus?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) minPrice: Long?,
        @RequestParam(required = false) maxPrice: Long?,
    ): String {
        val filter = ProductQueryFilterRequest(status = status, name = name, category = category, minPrice = minPrice, maxPrice = maxPrice)
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        model.addAttribute("products", productService.listProducts(filter, pageable))
        model.addAttribute("filter", filter)
        model.addAttribute("statuses", ProductStatus.entries)
        return "admin/products"
    }

    @GetMapping("/{id}")
    fun detail(
        @PathVariable id: Long,
        model: Model,
    ): String {
        model.addAttribute("product", productService.findById(id))
        return "admin/product-detail"
    }

    @PostMapping("/{id}/approve")
    fun approve(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ) = transition(id, redirectAttributes) { productService.approve(id) }

    @PostMapping("/{id}/reject")
    fun reject(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ) = transition(id, redirectAttributes) { productService.reject(id) }

    @PostMapping("/{id}/sold-out")
    fun soldOut(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ) = transition(id, redirectAttributes) { productService.markSoldOut(id) }

    @PostMapping("/{id}/resume-sale")
    fun resumeSale(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ) = transition(id, redirectAttributes) { productService.resumeSale(id) }

    @PostMapping("/{id}/discontinue")
    fun discontinue(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes,
    ) = transition(id, redirectAttributes) { productService.discontinue(id) }

    private fun transition(
        id: Long,
        redirectAttributes: RedirectAttributes,
        action: () -> Unit,
    ): String {
        runCatching(action)
            .onSuccess { redirectAttributes.addFlashAttribute("success", "상태가 변경됐습니다.") }
            .onFailure { redirectAttributes.addFlashAttribute("error", it.message ?: "오류가 발생했습니다.") }
        return "redirect:/admin/products/$id"
    }
}
