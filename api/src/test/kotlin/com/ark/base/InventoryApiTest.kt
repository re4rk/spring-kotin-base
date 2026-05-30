package com.ark.base

import com.ark.base.support.ApiIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.patch

class InventoryApiTest : ApiIntegrationTest() {
    @Test
    fun `판매자는 자신의 상품 재고를 차감할 수 있다`() {
        val productId = createProduct(sellerToken)
        val inventoryId = inventoryIdFor(productId)

        mockMvc
            .patch("/inventories/$inventoryId/decrease") {
                contentType = MediaType.APPLICATION_JSON
                header(HttpHeaders.AUTHORIZATION, bearer(sellerToken))
                content = """{ "quantity": 3 }"""
            }.andExpect {
                status { isNoContent() }
            }
    }

    @Test
    fun `다른 사용자는 재고를 차감할 수 없다`() {
        val productId = createProduct(sellerToken)
        val inventoryId = inventoryIdFor(productId)

        mockMvc
            .patch("/inventories/$inventoryId/decrease") {
                contentType = MediaType.APPLICATION_JSON
                header(HttpHeaders.AUTHORIZATION, bearer(buyerToken))
                content = """{ "quantity": 1 }"""
            }.andExpect {
                status { isForbidden() }
                jsonPath("$.code") { value("ACCESS_DENIED") }
            }
    }
}
