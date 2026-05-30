package com.ark.base.application

import com.ark.base.inventory.Inventory
import com.ark.base.inventory.InventoryRepository
import com.ark.base.product.Product
import com.ark.base.product.ProductRepository
import com.ark.base.product.getById
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val inventoryRepository: InventoryRepository,
) {
    @Transactional(readOnly = true)
    fun findAllByFilter(request: ProductQueryFilterRequest): List<ProductResponse> =
        productRepository.findAllByFilter(request.toQueryFilter()).map { product -> toResponse(product) }

    @Transactional(readOnly = true)
    fun findById(productId: Long): ProductResponse {
        val product = productRepository.getById(productId)
        return toResponse(product)
    }

    @Transactional
    fun create(request: ProductCreateRequest): ProductResponse {
        val product = productRepository.save(Product(name = request.name, price = request.price))
        val inventory = inventoryRepository.save(Inventory(productId = product.id, stock = request.initialStock))
        return ProductResponse.from(product, inventory.stock)
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
        val product = productRepository.getById(productId)
        action(product)
        return toResponse(product)
    }

    private fun toResponse(product: Product): ProductResponse {
        val stock = inventoryRepository.findByProductId(product.id)?.stock ?: 0
        return ProductResponse.from(product, stock)
    }
}
