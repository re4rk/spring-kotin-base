package com.ark.base

import com.ark.base.support.ApiIntegrationTest
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Assertions.assertNotEquals
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
                jsonPath("$.data.refreshToken") { exists() }
            }
    }

    @Test
    fun `리프레시 토큰으로 액세스 토큰을 재발급한다`() {
        val loginResult =
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
                }.andReturn()

        val refreshToken = JsonPath.read<String>(loginResult.response.contentAsString, "$.data.refreshToken")

        val refreshResult =
            mockMvc
                .post("/auth/refresh") {
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        """
                        {
                          "refreshToken": "$refreshToken"
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.data.accessToken") { exists() }
                    jsonPath("$.data.refreshToken") { exists() }
                }.andReturn()

        val newRefreshToken = JsonPath.read<String>(refreshResult.response.contentAsString, "$.data.refreshToken")
        assertNotEquals(refreshToken, newRefreshToken)
    }

    @Test
    fun `유효하지 않은 리프레시 토큰이면 400을 반환한다`() {
        mockMvc
            .post("/auth/refresh") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {
                      "refreshToken": "invalid-token"
                    }
                    """.trimIndent()
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.code") { value("USER_REFRESH_TOKEN_INVALID") }
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
