package com.ark.base.application

import com.ark.base.inventory.Inventory
import com.ark.base.inventory.InventoryRepository
import com.ark.base.product.Product
import com.ark.base.product.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val inventoryRepository: InventoryRepository,
) {
    @Transactional(readOnly = true)
    fun findAll(): List<ProductResponse> =
        productRepository.findAll().map { product ->
            val stock = inventoryRepository.findByProductId(product.id)?.stock ?: 0
            ProductResponse.from(product, stock)
        }

    @Transactional(readOnly = true)
    fun findById(productId: Long): ProductResponse {
        val product = productRepository.getById(productId)
        val stock = inventoryRepository.findByProductId(productId)?.stock ?: 0
        return ProductResponse.from(product, stock)
    }

    @Transactional
    fun create(request: ProductCreateRequest): ProductResponse {
        val product = productRepository.save(Product(name = request.name, price = request.price))
        val inventory = inventoryRepository.save(Inventory(productId = product.id, stock = request.initialStock))
        return ProductResponse.from(product, inventory.stock)
    }
}
