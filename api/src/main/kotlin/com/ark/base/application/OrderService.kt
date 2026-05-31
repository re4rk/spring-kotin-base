package com.ark.base.application

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.order.OrderRepository
import com.ark.base.order.findByIdOrThrow
import com.ark.base.product.ProductRepository
import com.ark.base.product.findByIdOrThrow
import com.ark.base.product.option.ProductSkuRepository
import com.ark.base.product.option.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val productSkuRepository: ProductSkuRepository,
) {
    @Transactional
    fun place(
        request: OrderPlaceRequest,
        buyerId: Long,
    ): OrderResponse {
        val product = productRepository.findByIdOrThrow(request.productId)
        val sku = productSkuRepository.findByIdOrThrow(request.skuId)

        if (!product.isOrderable) throw BaseException(ErrorCode.PRODUCT_INVALID_STATUS)

        sku.decreaseStock(request.quantity)
        val order = orderRepository.save(request.toOrder(product, buyerId))

        return OrderResponse.from(order)
    }

    @Transactional
    fun cancel(orderId: Long): OrderResponse {
        val order = orderRepository.findByIdOrThrow(orderId)
        val sku = productSkuRepository.findByIdOrThrow(order.skuId)

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
