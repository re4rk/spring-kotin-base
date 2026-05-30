package com.ark.base.product

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProductTest {
    @Test
    fun `DRAFT 상품을 submit 하면 PENDING이 된다`() {
        val product = product()

        product.submit()

        assertEquals(ProductStatus.PENDING, product.status)
    }

    @Test
    fun `PENDING 상품을 approve 하면 ON_SALE이 된다`() {
        val product = product()
        product.submit()

        product.approve()

        assertEquals(ProductStatus.ON_SALE, product.status)
    }

    @Test
    fun `허용되지 않은 상태 전환은 PRODUCT_INVALID_STATUS 예외를 던진다`() {
        val product = product()

        val exception = assertThrows<BaseException> { product.approve() }

        assertEquals(ErrorCode.PRODUCT_INVALID_STATUS, exception.errorCode)
    }

    @Test
    fun `이미 DISCONTINUED인 상품을 다시 discontinue 하면 PRODUCT_ALREADY_DISCONTINUED 예외를 던진다`() {
        val product = product()
        product.discontinue()

        val exception = assertThrows<BaseException> { product.discontinue() }

        assertEquals(ErrorCode.PRODUCT_ALREADY_DISCONTINUED, exception.errorCode)
    }

    @Test
    fun `SOLD_OUT 상품을 resumeSale 하면 ON_SALE이 된다`() {
        val product = product()
        product.submit()
        product.approve()
        product.markSoldOut()

        product.resumeSale()

        assertEquals(ProductStatus.ON_SALE, product.status)
    }

    private fun product() = Product(name = "상품", price = 10_000)
}
