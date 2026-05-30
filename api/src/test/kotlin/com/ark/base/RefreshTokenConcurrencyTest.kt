package com.ark.base

import com.ark.base.support.RedisIntegrationTest
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest(classes = [BaseApplication::class])
@AutoConfigureMockMvc
class RefreshTokenConcurrencyTest : RedisIntegrationTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `동시 refresh 요청은 하나만 성공한다`() {
        val id = UUID.randomUUID().toString().take(8)
        val email = "refresh-$id@test.com"
        register(email)
        val refreshToken = loginRefreshToken(email)

        val statusCodes = concurrentRefresh(refreshToken)

        assertEquals(2, statusCodes.size)
        assertEquals(1, statusCodes.count { it == HttpStatus.OK.value() })
        assertEquals(1, statusCodes.count { it == HttpStatus.BAD_REQUEST.value() })
    }

    private fun register(email: String) {
        mockMvc
            .post("/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {
                      "email": "$email",
                      "name": "Refresh User",
                      "password": "password123"
                    }
                    """.trimIndent()
            }.andExpect {
                status { isCreated() }
            }
    }

    private fun loginRefreshToken(email: String): String {
        val result =
            mockMvc
                .post("/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        """
                        {
                          "email": "$email",
                          "password": "password123"
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isOk() }
                }.andReturn()
        return JsonPath.read(result.response.contentAsString, "$.data.refreshToken")
    }

    private fun concurrentRefresh(refreshToken: String): List<Int> {
        val latch = CountDownLatch(2)
        val statusCodes = ConcurrentLinkedQueue<Int>()
        val executor = Executors.newFixedThreadPool(2)

        repeat(2) {
            executor.submit {
                try {
                    statusCodes.add(
                        mockMvc
                            .post("/auth/refresh") {
                                contentType = MediaType.APPLICATION_JSON
                                content =
                                    """
                                    {
                                      "refreshToken": "$refreshToken"
                                    }
                                    """.trimIndent()
                            }.andReturn()
                            .response
                            .status,
                    )
                } finally {
                    latch.countDown()
                }
            }
        }

        check(latch.await(10, TimeUnit.SECONDS)) { "timed out" }
        executor.shutdown()
        return statusCodes.toList()
    }
}
