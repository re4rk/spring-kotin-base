package com.ark.base

import com.ark.base.common.JwtProvider
import com.ark.base.inventory.InventoryRepository
import com.ark.base.support.ApiTestClient
import com.ark.base.BaseApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
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
class InventoryConcurrencyTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @Autowired
    private lateinit var inventoryRepository: InventoryRepository

    private lateinit var api: ApiTestClient
    private var productId: Long = 0
    private lateinit var buyer1Token: String
    private lateinit var buyer2Token: String

    @BeforeEach
    fun setUp() {
        api = ApiTestClient(mockMvc, jwtProvider)
        val id = UUID.randomUUID().toString().take(8)
        val sellerToken = api.registerAndLogin("seller-$id@test.com", "Seller")
        buyer1Token = api.registerAndLogin("buyer1-$id@test.com", "Buyer1")
        buyer2Token = api.registerAndLogin("buyer2-$id@test.com", "Buyer2")
        productId = api.createPublishedProduct(sellerToken, initialStock = 1)
    }

    @Test
    fun `재고 1개에 연속 주문 2건이면 두 번째는 409를 반환한다`() {
        placeOrder(buyer1Token).andExpect { status { isCreated() } }

        placeOrder(buyer2Token).andExpect { status { isConflict() } }

        assertEquals(0, inventoryRepository.findByProductId(productId)!!.stock)
    }

    @Test
    fun `재고 1개에 동시 주문 2건이면 1건만 성공한다`() {
        val statusCodes = concurrentPlaceOrders(tokens = listOf(buyer1Token, buyer2Token), productId = productId)

        assertEquals(2, statusCodes.size, "statusCodes=$statusCodes")
        assertEquals(1, statusCodes.count { it == HttpStatus.CREATED.value() }, "statusCodes=$statusCodes")
        assertEquals(1, statusCodes.count { it == HttpStatus.CONFLICT.value() }, "statusCodes=$statusCodes")

        assertEquals(0, inventoryRepository.findByProductId(productId)!!.stock)
    }

    @Test
    fun `재고 100개에 동시 주문 1000건이면 100건만 성공한다`() {
        val id = UUID.randomUUID().toString().take(8)
        val sellerToken = api.registerAndLogin("seller-load-$id@test.com", "Seller")
        val buyerToken = api.registerAndLogin("buyer-load-$id@test.com", "Buyer")
        val loadProductId = api.createPublishedProduct(sellerToken, initialStock = 100)

        val statusCodes =
            concurrentPlaceOrders(
                tokens = List(1000) { buyerToken },
                productId = loadProductId,
                threadPoolSize = 100,
                awaitSeconds = 120,
            )

        assertEquals(1000, statusCodes.size, "statusCodes size mismatch")
        assertEquals(100, statusCodes.count { it == HttpStatus.CREATED.value() }, "success count mismatch")
        assertEquals(900, statusCodes.count { it == HttpStatus.CONFLICT.value() }, "conflict count mismatch")

        assertEquals(0, inventoryRepository.findByProductId(loadProductId)!!.stock)
    }

    private fun concurrentPlaceOrders(
        tokens: List<String>,
        productId: Long,
        threadPoolSize: Int = tokens.size.coerceAtMost(50),
        awaitSeconds: Long = 30,
    ): List<Int> {
        val latch = CountDownLatch(tokens.size)
        val statusCodes = ConcurrentLinkedQueue<Int>()
        val executor = Executors.newFixedThreadPool(threadPoolSize)

        tokens.forEach { token ->
            executor.submit {
                try {
                    statusCodes.add(placeOrder(token, productId).andReturn().response.status)
                } finally {
                    latch.countDown()
                }
            }
        }

        assertTrue(latch.await(awaitSeconds, TimeUnit.SECONDS), "timed out after ${awaitSeconds}s")
        executor.shutdown()

        return statusCodes.toList()
    }

    private fun placeOrder(
        token: String,
        targetProductId: Long = productId,
    ) =
        mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            content =
                """
                {
                  "productId": $targetProductId,
                  "quantity": 1
                }
                """.trimIndent()
        }
}
