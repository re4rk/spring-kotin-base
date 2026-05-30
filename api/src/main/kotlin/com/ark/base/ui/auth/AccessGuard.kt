package com.ark.base.ui.auth

import com.ark.base.common.BaseException
import com.ark.base.common.CurrentUserProvider
import com.ark.base.common.ErrorCode
import com.ark.base.inventory.InventoryRepository
import com.ark.base.inventory.getById
import com.ark.base.order.Order
import com.ark.base.order.OrderRepository
import com.ark.base.order.getById
import com.ark.base.product.Product
import com.ark.base.product.ProductRepository
import com.ark.base.product.getById
import com.ark.base.user.UserRepository
import org.springframework.stereotype.Component

@Component
class AccessGuard(
    private val currentUserProvider: CurrentUserProvider,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val inventoryRepository: InventoryRepository,
    private val userRepository: UserRepository,
) {
    fun currentUserId(): Long = currentUserProvider.getUserId()

    fun requireSelf(targetUserId: Long) {
        if (currentUserId() != targetUserId) throw BaseException(ErrorCode.ACCESS_DENIED)
    }

    fun requireSelfByEmail(email: String) {
        val user = userRepository.findByEmail(email) ?: return
        requireSelf(user.id)
    }

    fun requireProductOwner(productId: Long) {
        requireProductOwner(productRepository.getById(productId))
    }

    fun requireProductOwnerByInventory(inventoryId: Long) {
        requireProductOwner(inventoryRepository.getById(inventoryId).productId)
    }

    fun requireOrderBuyer(orderId: Long) {
        requireOrderBuyer(orderRepository.getById(orderId))
    }

    fun requireOrderSeller(orderId: Long) {
        val order = orderRepository.getById(orderId)
        requireOrderSeller(order, productRepository.getById(order.productId))
    }

    private fun requireProductOwner(product: Product) {
        val ownerId = product.createdBy?.toLongOrNull()
        if (ownerId == null || ownerId != currentUserId()) throw BaseException(ErrorCode.ACCESS_DENIED)
    }

    private fun requireOrderBuyer(order: Order) = requireSelf(order.userId)

    private fun requireOrderSeller(
        order: Order,
        product: Product,
    ) {
        if (order.productId != product.id) throw BaseException(ErrorCode.ACCESS_DENIED)
        requireProductOwner(product)
    }
}
