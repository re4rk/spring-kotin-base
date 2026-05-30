package com.ark.base.ui

import com.ark.base.application.ProductCreateRequest
import com.ark.base.application.ProductQueryFilterRequest
import com.ark.base.application.ProductResponse
import com.ark.base.application.ProductService
import com.ark.base.ui.auth.AccessType
import com.ark.base.ui.auth.Authorize
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
) {
    @GetMapping
    fun findAll(
        @ModelAttribute request: ProductQueryFilterRequest,
    ): List<ProductResponse> = productService.findAllByFilter(request)

    @GetMapping("/{productId}")
    fun findById(
        @PathVariable productId: Long,
    ): ProductResponse = productService.findById(productId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody request: ProductCreateRequest,
    ): ProductResponse = productService.create(request)

    @Authorize(AccessType.PRODUCT_OWNER, param = "productId")
    @PatchMapping("/{productId}/submit")
    fun submit(
        @PathVariable productId: Long,
    ): ProductResponse = productService.submit(productId)

    @Authorize(AccessType.PRODUCT_OWNER, param = "productId")
    @PatchMapping("/{productId}/approve")
    fun approve(
        @PathVariable productId: Long,
    ): ProductResponse = productService.approve(productId)

    @Authorize(AccessType.PRODUCT_OWNER, param = "productId")
    @PatchMapping("/{productId}/reject")
    fun reject(
        @PathVariable productId: Long,
    ): ProductResponse = productService.reject(productId)

    @Authorize(AccessType.PRODUCT_OWNER, param = "productId")
    @PatchMapping("/{productId}/sold-out")
    fun markSoldOut(
        @PathVariable productId: Long,
    ): ProductResponse = productService.markSoldOut(productId)

    @Authorize(AccessType.PRODUCT_OWNER, param = "productId")
    @PatchMapping("/{productId}/resume-sale")
    fun resumeSale(
        @PathVariable productId: Long,
    ): ProductResponse = productService.resumeSale(productId)

    @Authorize(AccessType.PRODUCT_OWNER, param = "productId")
    @PatchMapping("/{productId}/discontinue")
    fun discontinue(
        @PathVariable productId: Long,
    ): ProductResponse = productService.discontinue(productId)
}
