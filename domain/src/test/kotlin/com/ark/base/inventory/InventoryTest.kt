package com.ark.base.inventory

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InventoryTest {
    @Test
    fun `재고 엔티티는 낙관적 락을 위한 version을 갖는다`() {
        val inventory = Inventory(productId = 1L, stock = 10)

        assertEquals(0, inventory.version)
    }

    @Test
    fun `재고를 차감하면 수량이 줄어든다`() {
        val inventory = Inventory(productId = 1L, stock = 10)

        inventory.decrease(3)

        assertEquals(7, inventory.stock)
    }

    @Test
    fun `재고를 증가시키면 수량이 늘어난다`() {
        val inventory = Inventory(productId = 1L, stock = 5)

        inventory.increase(4)

        assertEquals(9, inventory.stock)
    }

    @Test
    fun `재고가 부족하면 STOCK_INSUFFICIENT 예외를 던진다`() {
        val inventory = Inventory(productId = 1L, stock = 2)

        val exception = assertThrows<BaseException> { inventory.decrease(3) }

        assertEquals(ErrorCode.STOCK_INSUFFICIENT, exception.errorCode)
        assertEquals(2, inventory.stock)
    }

    @Test
    fun `재고를 전량 차감하면 0이 된다`() {
        val inventory = Inventory(productId = 42L, stock = 2)

        inventory.decrease(2)

        assertEquals(0, inventory.stock)
    }
}
