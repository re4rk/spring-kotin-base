package com.ark.base

import com.ark.base.support.ApiIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch

class UserApiTest : ApiIntegrationTest() {
    @Test
    fun `인증된 사용자는 본인 정보를 조회할 수 있다`() {
        mockMvc
            .get("/users/me") {
                header(HttpHeaders.AUTHORIZATION, bearer(sellerToken))
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.email") { value("seller@test.com") }
                jsonPath("$.data.name") { value("Seller") }
            }
    }

    @Test
    fun `본인 이메일로 사용자 정보를 조회할 수 있다`() {
        mockMvc
            .get("/users") {
                param("email", "seller@test.com")
                header(HttpHeaders.AUTHORIZATION, bearer(sellerToken))
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.email") { value("seller@test.com") }
            }
    }

    @Test
    fun `다른 사용자 이메일은 조회할 수 없다`() {
        mockMvc
            .get("/users") {
                param("email", "buyer@test.com")
                header(HttpHeaders.AUTHORIZATION, bearer(sellerToken))
            }.andExpect {
                status { isForbidden() }
                jsonPath("$.code") { value("ACCESS_DENIED") }
            }
    }

    @Test
    fun `본인 비밀번호를 변경할 수 있다`() {
        mockMvc
            .patch("/users/$sellerId/password") {
                contentType = MediaType.APPLICATION_JSON
                header(HttpHeaders.AUTHORIZATION, bearer(sellerToken))
                content =
                    """
                    {
                      "currentPassword": "password123",
                      "newPassword": "new-password123"
                    }
                    """.trimIndent()
            }.andExpect {
                status { isNoContent() }
            }
    }

    @Test
    fun `다른 사용자 비밀번호는 변경할 수 없다`() {
        mockMvc
            .patch("/users/$buyerId/password") {
                contentType = MediaType.APPLICATION_JSON
                header(HttpHeaders.AUTHORIZATION, bearer(sellerToken))
                content =
                    """
                    {
                      "currentPassword": "password123",
                      "newPassword": "new-password123"
                    }
                    """.trimIndent()
            }.andExpect {
                status { isForbidden() }
                jsonPath("$.code") { value("ACCESS_DENIED") }
            }
    }

    @Test
    fun `본인 계정을 삭제할 수 있다`() {
        val token = registerAndLogin("delete-me@test.com", "Delete Me")

        mockMvc
            .delete("/users/${loginUserId(token)}") {
                header(HttpHeaders.AUTHORIZATION, bearer(token))
            }.andExpect {
                status { isNoContent() }
            }
    }
}
