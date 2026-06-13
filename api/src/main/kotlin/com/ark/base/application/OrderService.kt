package com.ark.base.application

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.order.Order
import com.ark.base.order.OrderRepository
import com.ark.base.order.findByIdOrThrow
import com.ark.base.product.ProductRepository
import com.ark.base.product.findByIdOrThrow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
) {
    @Transactional(readOnly = true)
    fun listOrders(pageable: Pageable): Page<Order> = orderRepository.findAll(pageable)

    @Transactional
    fun place(
        request: OrderPlaceRequest,
        buyerId: Long,
    ): OrderResponse {
        val product = productRepository.findByIdOrThrow(request.productId)
        if (!product.isOrderable) throw BaseException(ErrorCode.PRODUCT_INVALID_STATUS)
        val sku = product.findSkuOrThrow(request.skuId)
        sku.decreaseStock(request.quantity)
        val order = orderRepository.save(request.toOrder(product, buyerId))
        return OrderResponse.from(order)
    }

    @Transactional
    fun cancel(orderId: Long): OrderResponse {
        val order = orderRepository.findByIdOrThrow(orderId)
        val product = productRepository.findByIdOrThrow(order.productId)
        val sku = product.findSkuOrThrow(order.skuId)
        order.cancel()
        sku.increaseStock(order.quantity)
        return OrderResponse.from(order)
    }

    @Transactional
    fun confirm(orderId: Long): OrderResponse {
        val order = orderRepository.findByIdOrThrow(orderId)
        order.confirm()
        return OrderResponse.from(order)
    }

    @Transactional
    fun ship(orderId: Long): OrderResponse {
        val order = orderRepository.findByIdOrThrow(orderId)
        order.ship()
        return OrderResponse.from(order)
    }

    @Transactional
    fun deliver(orderId: Long): OrderResponse {
        val order = orderRepository.findByIdOrThrow(orderId)
        order.deliver()
        return OrderResponse.from(order)
    }
}
