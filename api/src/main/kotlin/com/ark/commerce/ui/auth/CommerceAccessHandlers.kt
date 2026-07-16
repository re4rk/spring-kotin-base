package com.ark.commerce.ui.auth

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.ui.auth.AccessGuard
import com.ark.base.ui.auth.AccessType
import com.ark.base.ui.auth.AccessTypeHandler
import com.ark.commerce.order.Order
import com.ark.commerce.order.OrderRepository
import com.ark.commerce.order.findByIdOrThrow
import com.ark.commerce.product.Product
import com.ark.commerce.product.ProductRepository
import com.ark.commerce.product.findByIdOrThrow
import org.springframework.stereotype.Component

@Component
class ProductOwnerAccessHandler(
    private val accessGuard: AccessGuard,
    private val productRepository: ProductRepository,
) : AccessTypeHandler {
    override val type = AccessType.PRODUCT_OWNER

    override fun authorize(param: Any) {
        requireProductOwner(productRepository.findByIdOrThrow(param as Long))
    }

    private fun requireProductOwner(product: Product) {
        val ownerId = product.createdBy?.toLongOrNull()
        if (ownerId == null || ownerId != accessGuard.currentUserId()) {
            throw BaseException(ErrorCode.ACCESS_DENIED)
        }
    }
}

@Component
class OrderBuyerAccessHandler(
    private val accessGuard: AccessGuard,
    private val orderRepository: OrderRepository,
) : AccessTypeHandler {
    override val type = AccessType.ORDER_BUYER

    override fun authorize(param: Any) {
        requireOrderBuyer(orderRepository.findByIdOrThrow(param as Long))
    }

    private fun requireOrderBuyer(order: Order) = accessGuard.requireSelf(order.userId)
}

@Component
class OrderSellerAccessHandler(
    private val accessGuard: AccessGuard,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
) : AccessTypeHandler {
    override val type = AccessType.ORDER_SELLER

    override fun authorize(param: Any) {
        val order = orderRepository.findByIdOrThrow(param as Long)
        val product = productRepository.findByIdOrThrow(order.productId)
        if (order.productId != product.id) throw BaseException(ErrorCode.ACCESS_DENIED)
        val ownerId = product.createdBy?.toLongOrNull()
        if (ownerId == null || ownerId != accessGuard.currentUserId()) {
            throw BaseException(ErrorCode.ACCESS_DENIED)
        }
    }
}
