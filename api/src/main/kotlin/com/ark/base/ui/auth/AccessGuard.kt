package com.ark.base.ui.auth

import com.ark.base.common.BaseException
import com.ark.base.common.CurrentUserProvider
import com.ark.base.common.ErrorCode
import com.ark.commerce.order.Order
import com.ark.commerce.order.OrderRepository
import com.ark.commerce.order.findByIdOrThrow
import com.ark.commerce.product.Product
import com.ark.commerce.product.ProductRepository
import com.ark.commerce.product.findByIdOrThrow
import com.ark.base.user.UserRepository
import org.springframework.stereotype.Component

@Component
class AccessGuard(
    private val currentUserProvider: CurrentUserProvider,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
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
        requireProductOwner(productRepository.findByIdOrThrow(productId))
    }

    fun requireOrderBuyer(orderId: Long) {
        requireOrderBuyer(orderRepository.findByIdOrThrow(orderId))
    }

    fun requireOrderSeller(orderId: Long) {
        val order = orderRepository.findByIdOrThrow(orderId)
        requireOrderSeller(order, productRepository.findByIdOrThrow(order.productId))
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
