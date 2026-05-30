package com.ark.base.order

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class OrderStatusTest {
    @Test
    fun `PLACED는 CONFIRMED 또는 CANCELLED로 전환할 수 있다`() {
        assertTrue(OrderStatus.PLACED.canTransitionTo(OrderStatus.CONFIRMED))
        assertTrue(OrderStatus.PLACED.canTransitionTo(OrderStatus.CANCELLED))
        assertFalse(OrderStatus.PLACED.canTransitionTo(OrderStatus.SHIPPING))
    }

    @Test
    fun `PLACED와 CONFIRMED만 취소 가능하다`() {
        assertTrue(OrderStatus.PLACED.isCancellable)
        assertTrue(OrderStatus.CONFIRMED.isCancellable)
        assertFalse(OrderStatus.SHIPPING.isCancellable)
        assertFalse(OrderStatus.DELIVERED.isCancellable)
        assertFalse(OrderStatus.CANCELLED.isCancellable)
    }
}
