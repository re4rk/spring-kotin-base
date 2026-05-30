package com.ark.base.order

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderTest {
    @Test
    fun `PLACED 주문을 confirm 하면 CONFIRMED가 된다`() {
        val order = order()

        order.confirm()

        assertEquals(OrderStatus.CONFIRMED, order.status)
    }

    @Test
    fun `주문 흐름 confirm ship deliver 가 순서대로 동작한다`() {
        val order = order()

        order.confirm()
        order.ship()
        order.deliver()

        assertEquals(OrderStatus.DELIVERED, order.status)
    }

    @Test
    fun `PLACED 주문을 cancel 하면 CANCELLED가 된다`() {
        val order = order()

        order.cancel()

        assertEquals(OrderStatus.CANCELLED, order.status)
    }

    @Test
    fun `이미 취소된 주문을 다시 cancel 하면 ORDER_ALREADY_CANCELLED 예외를 던진다`() {
        val order = order()
        order.cancel()

        val exception = assertThrows<BaseException> { order.cancel() }

        assertEquals(ErrorCode.ORDER_ALREADY_CANCELLED, exception.errorCode)
    }

    @Test
    fun `배송 중인 주문은 cancel 할 수 없다`() {
        val order = order()
        order.confirm()
        order.ship()

        val exception = assertThrows<BaseException> { order.cancel() }

        assertEquals(ErrorCode.ORDER_INVALID_STATUS, exception.errorCode)
    }

    private fun order() =
        Order(
            userId = 1L,
            productId = 10L,
            productName = "상품",
            quantity = 2,
        )
}
