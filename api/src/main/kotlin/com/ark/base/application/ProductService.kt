package com.ark.base.application

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.product.Product
import com.ark.base.product.ProductRepository
import com.ark.base.product.findByIdOrThrow
import com.ark.base.product.option.ProductSku
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    @Transactional(readOnly = true)
    fun findAllByFilter(request: ProductQueryFilterRequest): List<ProductResponse> =
        productRepository.findAllByFilter(request.toQueryFilter()).map { ProductResponse.from(it) }

    @Transactional(readOnly = true)
    fun findById(productId: Long): ProductResponse {
        val product = productRepository.findByIdOrThrow(productId)
        return ProductResponse.from(product)
    }

    @Transactional
    fun create(request: ProductCreateRequest): ProductResponse {
        val product = productRepository.save(request.toProduct())
        return ProductResponse.from(product)
    }

    @Transactional
    fun addOptionGroup(
        productId: Long,
        request: ProductOptionGroupCreateRequest,
    ): ProductResponse {
        val product = productRepository.findByIdOrThrow(productId)
        product.addOptionGroup(request.toOptionGroup())
        productRepository.flush()
        return ProductResponse.from(product)
    }

    @Transactional
    fun removeOptionGroup(
        productId: Long,
        groupId: Long,
    ): ProductResponse {
        val product = productRepository.findByIdOrThrow(productId)
        product.removeOptionGroup(groupId)
        return ProductResponse.from(product)
    }

    @Transactional
    fun addSku(
        productId: Long,
        request: ProductSkuCreateRequest,
    ): ProductResponse {
        val product = productRepository.findByIdOrThrow(productId)
        val allOptions = product.optionGroups.flatMap { it.options }
        val options =
            request.optionIds.map { id ->
                allOptions.find { it.id == id } ?: throw BaseException(ErrorCode.PRODUCT_OPTION_NOT_FOUND)
            }
        product.addSku(ProductSku(stock = request.stock, extraPrice = request.extraPrice, options = options.toMutableList()))
        productRepository.flush()
        return ProductResponse.from(product)
    }

    @Transactional
    fun removeSku(
        productId: Long,
        skuId: Long,
    ): ProductResponse {
        val product = productRepository.findByIdOrThrow(productId)
        product.removeSku(skuId)
        return ProductResponse.from(product)
    }

    @Transactional
    fun update(
        productId: Long,
        request: ProductUpdateRequest,
    ): ProductResponse {
        val product = productRepository.findByIdOrThrow(productId)
        product.update(
            name = request.name,
            price = request.price,
            description = request.description,
            category = request.category,
            thumbnailUrl = request.thumbnailUrl,
        )
        return ProductResponse.from(product)
    }

    @Transactional
    fun submit(productId: Long): ProductResponse = transition(productId) { it.submit() }

    @Transactional
    fun approve(productId: Long): ProductResponse = transition(productId) { it.approve() }

    @Transactional
    fun reject(productId: Long): ProductResponse = transition(productId) { it.reject() }

    @Transactional
    fun markSoldOut(productId: Long): ProductResponse = transition(productId) { it.markSoldOut() }

    @Transactional
    fun resumeSale(productId: Long): ProductResponse = transition(productId) { it.resumeSale() }

    @Transactional
    fun discontinue(productId: Long): ProductResponse = transition(productId) { it.discontinue() }

    private fun transition(
        productId: Long,
        action: (Product) -> Unit,
    ): ProductResponse {
        val product = productRepository.findByIdOrThrow(productId)
        action(product)
        return ProductResponse.from(product)
    }
}
