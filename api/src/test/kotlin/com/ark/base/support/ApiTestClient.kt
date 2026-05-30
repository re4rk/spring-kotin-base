package com.ark.base.support

import com.ark.base.common.JwtProvider
import com.jayway.jsonpath.JsonPath
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

class ApiTestClient(
    private val mockMvc: MockMvc,
    private val jwtProvider: JwtProvider,
) {
    fun registerAndLogin(
        email: String,
        name: String,
        password: String = "password123",
    ): String {
        register(email, name, password)
        return login(email, password)
    }

    fun loginUserId(token: String): Long = jwtProvider.parseUserId(token)

    fun createPublishedProduct(
        sellerToken: String,
        initialStock: Int = 10,
    ): Long {
        val productId = createProduct(sellerToken, initialStock = initialStock)
        publishProduct(sellerToken, productId)
        return productId
    }

    private fun register(
        email: String,
        name: String,
        password: String,
    ) {
        mockMvc
            .post("/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {
                      "email": "$email",
                      "name": "$name",
                      "password": "$password"
                    }
                    """.trimIndent()
            }.andExpect {
                status { isCreated() }
            }
    }

    private fun login(
        email: String,
        password: String,
    ): String {
        val result =
            mockMvc
                .post("/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        """
                        {
                          "email": "$email",
                          "password": "$password"
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isOk() }
                }.andReturn()
        return JsonPath.read(result.response.contentAsString, "$.data.accessToken")
    }

    private fun createProduct(
        token: String,
        initialStock: Int,
    ): Long {
        val result =
            mockMvc
                .post("/products") {
                    contentType = MediaType.APPLICATION_JSON
                    header(HttpHeaders.AUTHORIZATION, bearer(token))
                    content =
                        """
                        {
                          "name": "Test Product",
                          "price": 10000,
                          "initialStock": $initialStock
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isCreated() }
                }.andReturn()
        return JsonPath.read(result.response.contentAsString, "$.data.id")
    }

    private fun publishProduct(
        token: String,
        productId: Long,
    ) {
        mockMvc
            .patch("/products/$productId/submit") {
                header(HttpHeaders.AUTHORIZATION, bearer(token))
            }.andExpect {
                status { isOk() }
            }
        mockMvc
            .patch("/products/$productId/approve") {
                header(HttpHeaders.AUTHORIZATION, bearer(token))
            }.andExpect {
                status { isOk() }
            }
    }

    private fun bearer(token: String): String = "Bearer $token"
}
