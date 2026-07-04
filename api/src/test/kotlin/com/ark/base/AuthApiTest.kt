package com.ark.base

import com.ark.base.support.ApiIntegrationTest
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class AuthApiTest : ApiIntegrationTest() {
    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

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
    fun `재로그인하면 이전 refresh token은 무효화된다`() {
        val firstRefreshToken = loginRefreshToken("seller@test.com")
        loginRefreshToken("seller@test.com")

        refreshWithToken(firstRefreshToken).andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("USER_REFRESH_TOKEN_INVALID") }
        }
    }

    @Test
    fun `사용된 refresh token 재사용 시 모든 세션이 무효화된다`() {
        val originalRefreshToken = loginRefreshToken("seller@test.com")
        val rotatedRefreshToken =
            JsonPath.read<String>(
                refreshWithToken(originalRefreshToken)
                    .andExpect { status { isOk() } }
                    .andReturn()
                    .response
                    .contentAsString,
                "$.data.refreshToken",
            )

        refreshWithToken(originalRefreshToken).andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("USER_REFRESH_TOKEN_REUSED") }
        }

        refreshWithToken(rotatedRefreshToken).andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("USER_REFRESH_TOKEN_INVALID") }
        }
    }

    @Test
    fun `만료된 리프레시 토큰으로 refresh하면 REFRESH_TOKEN_EXPIRED를 반환한다`() {
        val refreshToken = loginRefreshToken("seller@test.com")
        redisTemplate.delete("refresh-token:$refreshToken")

        refreshWithToken(refreshToken).andExpect {
            status { isUnauthorized() }
            jsonPath("$.code") { value("REFRESH_TOKEN_EXPIRED") }
        }
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
    fun `만료된 액세스 토큰으로 요청하면 ACCESS_TOKEN_EXPIRED를 반환한다`() {
        val expiredToken = expiredAccessToken(sellerId)

        mockMvc
            .post("/products") {
                contentType = MediaType.APPLICATION_JSON
                header(HttpHeaders.AUTHORIZATION, bearer(expiredToken))
                content =
                    """
                    {
                      "name": "Expired Token Product",
                      "price": 1000
                    }
                    """.trimIndent()
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.code") { value("ACCESS_TOKEN_EXPIRED") }
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

    private fun loginRefreshToken(
        email: String,
        password: String = "password123",
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
        return JsonPath.read(result.response.contentAsString, "$.data.refreshToken")
    }

    private fun refreshWithToken(refreshToken: String) =
        mockMvc.post("/auth/refresh") {
            contentType = MediaType.APPLICATION_JSON
            content =
                """
                {
                  "refreshToken": "$refreshToken"
                }
                """.trimIndent()
        }
}
