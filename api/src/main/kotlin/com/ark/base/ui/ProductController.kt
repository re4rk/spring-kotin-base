package com.ark.base.ui

import com.ark.base.application.ProductCreateRequest
import com.ark.base.application.ProductResponse
import com.ark.base.application.ProductService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
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
    fun findAll(): List<ProductResponse> = productService.findAll()

    @GetMapping("/{productId}")
    fun findById(
        @PathVariable productId: Long,
    ): ProductResponse = productService.findById(productId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody request: ProductCreateRequest,
    ): ProductResponse = productService.create(request)

    @PatchMapping("/{productId}/submit")
    fun submit(
        @PathVariable productId: Long,
    ): ProductResponse = productService.submit(productId)

    @PatchMapping("/{productId}/approve")
    fun approve(
        @PathVariable productId: Long,
    ): ProductResponse = productService.approve(productId)

    @PatchMapping("/{productId}/reject")
    fun reject(
        @PathVariable productId: Long,
    ): ProductResponse = productService.reject(productId)

    @PatchMapping("/{productId}/sold-out")
    fun markSoldOut(
        @PathVariable productId: Long,
    ): ProductResponse = productService.markSoldOut(productId)

    @PatchMapping("/{productId}/resume-sale")
    fun resumeSale(
        @PathVariable productId: Long,
    ): ProductResponse = productService.resumeSale(productId)

    @PatchMapping("/{productId}/discontinue")
    fun discontinue(
        @PathVariable productId: Long,
    ): ProductResponse = productService.discontinue(productId)
}
