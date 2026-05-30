package com.ark.base

import com.ark.base.support.ApiIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class AuthApiTest : ApiIntegrationTest() {
    @Test
    fun `회원가입에 성공한다`() {
        mockMvc
            .post("/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {
                      "email": "new@test.com",
                      "name": "New User",
                      "password": "password123"
                    }
                    """.trimIndent()
            }.andExpect {
                status { isCreated() }
                jsonPath("$.data.email") { value("new@test.com") }
                jsonPath("$.data.name") { value("New User") }
            }
    }

    @Test
    fun `중복 이메일로 회원가입하면 409를 반환한다`() {
        mockMvc
            .post("/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {
                      "email": "seller@test.com",
                      "name": "Duplicate",
                      "password": "password123"
                    }
                    """.trimIndent()
            }.andExpect {
                status { isConflict() }
                jsonPath("$.code") { value("USER_DUPLICATE_EMAIL") }
            }
    }

    @Test
    fun `로그인에 성공하면 토큰을 반환한다`() {
        mockMvc
            .post("/auth/login") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {
                      "email": "seller@test.com",
                      "password": "password123"
                    }
                    """.trimIndent()
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.accessToken") { exists() }
                jsonPath("$.data.user.email") { value("seller@test.com") }
            }
    }

    @Test
    fun `잘못된 비밀번호로 로그인하면 401을 반환한다`() {
        mockMvc
            .post("/auth/login") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {
                      "email": "seller@test.com",
                      "password": "wrong-password"
                    }
                    """.trimIndent()
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.code") { value("USER_LOGIN_FAILED") }
            }
    }
}
