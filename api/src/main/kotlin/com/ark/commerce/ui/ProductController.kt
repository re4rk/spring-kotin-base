package com.ark.commerce.ui

import com.ark.base.ui.auth.AccessType
import com.ark.base.ui.auth.Authorize
import com.ark.commerce.application.ProductCreateRequest
import com.ark.commerce.application.ProductOptionGroupCreateRequest
import com.ark.commerce.application.ProductQueryFilterRequest
import com.ark.commerce.application.ProductResponse
import com.ark.commerce.application.ProductService
import com.ark.commerce.application.ProductSkuCreateRequest
import com.ark.commerce.application.ProductUpdateRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
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
        @PageableDefault(size = 20, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): Page<ProductResponse> = productService.listProducts(request, pageable).map { ProductResponse.from(it) }

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
    @PatchMapping("/{productId}")
    fun update(
        @PathVariable productId: Long,
        @RequestBody request: ProductUpdateRequest,
    ): ProductResponse = productService.update(productId, request)

    @Authorize(AccessType.PRODUCT_OWNER, param = "productId")
    @PostMapping("/{productId}/option-groups")
    @ResponseStatus(HttpStatus.CREATED)
    fun addOptionGroup(
        @PathVariable productId: Long,
        @RequestBody request: ProductOptionGroupCreateRequest,
    ): ProductResponse = productService.addOptionGroup(productId, request)

    @Authorize(AccessType.PRODUCT_OWNER, param = "productId")
    @DeleteMapping("/{productId}/option-groups/{groupId}")
    fun removeOptionGroup(
        @PathVariable productId: Long,
        @PathVariable groupId: Long,
    ): ProductResponse = productService.removeOptionGroup(productId, groupId)

    @Authorize(AccessType.PRODUCT_OWNER, param = "productId")
    @PostMapping("/{productId}/skus")
    @ResponseStatus(HttpStatus.CREATED)
    fun addSku(
        @PathVariable productId: Long,
        @RequestBody request: ProductSkuCreateRequest,
    ): ProductResponse = productService.addSku(productId, request)

    @Authorize(AccessType.PRODUCT_OWNER, param = "productId")
    @DeleteMapping("/{productId}/skus/{skuId}")
    fun removeSku(
        @PathVariable productId: Long,
        @PathVariable skuId: Long,
    ): ProductResponse = productService.removeSku(productId, skuId)

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
