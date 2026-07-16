package com.ark.base.infra

import com.ark.ArkApplication
import com.ark.base.support.RedisIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(classes = [ArkApplication::class])
@AutoConfigureMockMvc
class ActuatorHealthTest : RedisIntegrationTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `actuator health is public and reports up dependencies`() {
        mockMvc
            .get("/actuator/health")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value("UP") }
                jsonPath("$.components.db.status") { value("UP") }
                jsonPath("$.components.redis.status") { value("UP") }
            }
    }

    @Test
    fun `readiness probe excludes minio in tests`() {
        mockMvc
            .get("/actuator/health/readiness")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value("UP") }
            }
    }
}
