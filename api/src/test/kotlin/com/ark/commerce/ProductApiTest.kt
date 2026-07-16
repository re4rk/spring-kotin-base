package com.ark.commerce

import com.ark.base.support.ApiIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import java.util.UUID

class ProductApiTest : ApiIntegrationTest() {
    @Test
    fun `상품 목록은 인증 없이 조회할 수 있다`() {
        val uniqueName = "Public List Product ${UUID.randomUUID()}"
        val productId = createProduct(sellerToken, name = uniqueName)

        mockMvc
            .get("/products") {
                accept = MediaType.APPLICATION_JSON
                param("name", uniqueName)
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.content") { isArray() }
                jsonPath("$.data.content.length()") { value(1) }
                jsonPath("$.data.content[0].id") { value(productId) }
            }
    }

    @Test
    fun `상품 등록은 인증이 필요하다`() {
        mockMvc
            .post("/products") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {
                      "name": "Unauthorized",
                      "price": 1000
                    }
                    """.trimIndent()
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.code") { value("UNAUTHORIZED") }
            }
    }

    @Test
    fun `판매자는 자신의 상품 상태를 변경할 수 있다`() {
        val productId = createProduct(sellerToken)

        mockMvc
            .patch("/products/$productId/submit") {
                header(HttpHeaders.AUTHORIZATION, bearer(sellerToken))
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.status") { value("PENDING") }
            }
    }

    @Test
    fun `다른 사용자는 상품 상태를 변경할 수 없다`() {
        val productId = createProduct(sellerToken)

        mockMvc
            .patch("/products/$productId/submit") {
                header(HttpHeaders.AUTHORIZATION, bearer(buyerToken))
            }.andExpect {
                status { isForbidden() }
                jsonPath("$.code") { value("ACCESS_DENIED") }
            }
    }
}
