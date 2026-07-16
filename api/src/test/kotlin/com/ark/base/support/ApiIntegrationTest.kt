package com.ark.base.support

import com.ark.ArkApplication
import com.ark.base.common.JwtProvider
import com.jayway.jsonpath.JsonPath
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@SpringBootTest(classes = [ArkApplication::class])
@AutoConfigureMockMvc
@Transactional
abstract class ApiIntegrationTest : RedisIntegrationTest() {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var jwtProvider: JwtProvider

    protected lateinit var sellerToken: String
    protected var sellerId: Long = 0
    protected lateinit var buyerToken: String
    protected var buyerId: Long = 0

    @BeforeEach
    fun setUpUsers() {
        sellerToken = registerAndLogin("seller@test.com", "Seller")
        sellerId = loginUserId(sellerToken)
        buyerToken = registerAndLogin("buyer@test.com", "Buyer")
        buyerId = loginUserId(buyerToken)
    }

    protected fun registerAndLogin(
        email: String,
        name: String,
        password: String = "password123",
    ): String {
        register(email, name, password)
        return login(email, password)
    }

    protected fun register(
        email: String,
        name: String,
        password: String = "password123",
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

    protected fun login(
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
        return JsonPath.read(result.response.contentAsString, "$.data.accessToken")
    }

    protected fun loginUserId(token: String): Long {
        val userId = jwtProvider.parseUserId(token)
        return userId
    }

    protected fun bearer(token: String): String = "Bearer $token"

    protected fun expiredAccessToken(userId: Long): String =
        Jwts
            .builder()
            .subject(userId.toString())
            .issuedAt(Date(System.currentTimeMillis() - 7_200_000))
            .expiration(Date(System.currentTimeMillis() - 3_600_000))
            .signWith(Keys.hmacShaKeyFor("test-secret-key-minimum-256-bits-long-for-hmac".toByteArray()))
            .compact()

    protected fun createProduct(
        token: String,
        name: String = "Test Product",
        price: Long = 10_000,
    ): Long {
        val result =
            mockMvc
                .post("/products") {
                    contentType = MediaType.APPLICATION_JSON
                    header(HttpHeaders.AUTHORIZATION, bearer(token))
                    content =
                        """
                        {
                          "name": "$name",
                          "price": $price
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isCreated() }
                }.andReturn()
        return JsonPath.read(result.response.contentAsString, "$.data.id")
    }

    protected fun publishProduct(
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

    protected fun createPublishedProduct(
        token: String = sellerToken,
        name: String = "Test Product",
        price: Long = 10_000,
    ): Long {
        val productId = createProduct(token, name, price)
        publishProduct(token, productId)
        return productId
    }

    protected fun createPublishedProductWithSku(
        token: String = sellerToken,
        stock: Int = 10,
    ): Pair<Long, Long> {
        val productId = createProduct(token)
        val optionResult =
            mockMvc
                .post("/products/$productId/option-groups") {
                    contentType = MediaType.APPLICATION_JSON
                    header(HttpHeaders.AUTHORIZATION, bearer(token))
                    content = """{"name":"기본","options":[{"name":"기본","sortOrder":0}]}"""
                }.andExpect { status { isCreated() } }
                .andReturn()
        val optionId: Int = JsonPath.read(optionResult.response.contentAsString, "$.data.optionGroups[0].options[0].id")
        val skuResult =
            mockMvc
                .post("/products/$productId/skus") {
                    contentType = MediaType.APPLICATION_JSON
                    header(HttpHeaders.AUTHORIZATION, bearer(token))
                    content = """{"optionIds":[$optionId],"stock":$stock,"extraPrice":0}"""
                }.andExpect { status { isCreated() } }
                .andReturn()
        val skuId: Int = JsonPath.read(skuResult.response.contentAsString, "$.data.skus[0].id")
        publishProduct(token, productId)
        return productId to skuId.toLong()
    }

    protected fun MvcResult.dataPath(path: String): Any = JsonPath.read(response.contentAsString, "$.data$path")
}
