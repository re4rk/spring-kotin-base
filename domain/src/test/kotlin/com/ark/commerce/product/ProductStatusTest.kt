package com.ark.commerce.product

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ProductStatusTest {
    @Test
    fun `DRAFT는 PENDING 또는 DISCONTINUED로만 전환할 수 있다`() {
        assertTrue(ProductStatus.DRAFT.canTransitionTo(ProductStatus.PENDING))
        assertTrue(ProductStatus.DRAFT.canTransitionTo(ProductStatus.DISCONTINUED))
        assertFalse(ProductStatus.DRAFT.canTransitionTo(ProductStatus.ON_SALE))
    }

    @Test
    fun `ON_SALE만 주문 가능하다`() {
        assertTrue(ProductStatus.ON_SALE.isOrderable)
        assertFalse(ProductStatus.DRAFT.isOrderable)
        assertFalse(ProductStatus.PENDING.isOrderable)
        assertFalse(ProductStatus.SOLD_OUT.isOrderable)
        assertFalse(ProductStatus.DISCONTINUED.isOrderable)
    }

    @Test
    fun `DISCONTINUED는 더 이상 전환할 수 없다`() {
        ProductStatus.entries
            .filter { it != ProductStatus.DISCONTINUED }
            .forEach { target ->
                assertFalse(ProductStatus.DISCONTINUED.canTransitionTo(target))
            }
    }
}
