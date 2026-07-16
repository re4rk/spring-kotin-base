package com.ark.commerce

import com.ark.base.support.ApiIntegrationTest
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

class OrderApiTest : ApiIntegrationTest() {
    @Test
    fun `구매자는 주문을 생성할 수 있다`() {
        val (productId, skuId) = createPublishedProductWithSku()

        mockMvc
            .post("/orders") {
                contentType = MediaType.APPLICATION_JSON
                header(HttpHeaders.AUTHORIZATION, bearer(buyerToken))
                content =
                    """
                    {
                      "productId": $productId,
                      "skuId": $skuId,
                      "quantity": 2
                    }
                    """.trimIndent()
            }.andExpect {
                status { isCreated() }
                jsonPath("$.data.userId") { value(buyerId) }
                jsonPath("$.data.productId") { value(productId) }
                jsonPath("$.data.skuId") { value(skuId) }
                jsonPath("$.data.status") { value("PLACED") }
            }
    }

    @Test
    fun `구매자는 자신의 주문을 취소할 수 있다`() {
        val (productId, skuId) = createPublishedProductWithSku()
        val orderId = placeOrder(productId, skuId)

        mockMvc
            .patch("/orders/$orderId/cancel") {
                header(HttpHeaders.AUTHORIZATION, bearer(buyerToken))
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.status") { value("CANCELLED") }
            }
    }

    @Test
    fun `판매자는 주문을 확정할 수 있다`() {
        val (productId, skuId) = createPublishedProductWithSku()
        val orderId = placeOrder(productId, skuId)

        mockMvc
            .patch("/orders/$orderId/confirm") {
                header(HttpHeaders.AUTHORIZATION, bearer(sellerToken))
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.status") { value("CONFIRMED") }
            }
    }

    @Test
    fun `구매자는 판매자 전용 주문 처리를 할 수 없다`() {
        val (productId, skuId) = createPublishedProductWithSku()
        val orderId = placeOrder(productId, skuId)

        mockMvc
            .patch("/orders/$orderId/confirm") {
                header(HttpHeaders.AUTHORIZATION, bearer(buyerToken))
            }.andExpect {
                status { isForbidden() }
                jsonPath("$.code") { value("ACCESS_DENIED") }
            }
    }

    private fun placeOrder(
        productId: Long,
        skuId: Long,
        quantity: Int = 1,
    ): Long {
        val result =
            mockMvc
                .post("/orders") {
                    contentType = MediaType.APPLICATION_JSON
                    header(HttpHeaders.AUTHORIZATION, bearer(buyerToken))
                    content =
                        """
                        {
                          "productId": $productId,
                          "skuId": $skuId,
                          "quantity": $quantity
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isCreated() }
                }.andReturn()
        return JsonPath.read(result.response.contentAsString, "$.data.id")
    }
}
