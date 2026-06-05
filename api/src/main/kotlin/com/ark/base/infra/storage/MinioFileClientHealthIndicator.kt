package com.ark.base.infra.storage

import com.ark.base.common.MinioProperties
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream

@Component
@ConditionalOnProperty(
    name = ["management.health.minio.enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
class MinioFileClientHealthIndicator(
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties,
) : HealthIndicator {
    override fun health(): Health =
        runCatching {
            val path = probeUploadAndDelete()
            Health
                .up()
                .withDetail("bucket", minioProperties.bucket)
                .withDetail("path", path)
                .build()
        }.getOrElse { ex ->
            Health
                .down(ex)
                .withDetail("bucket", minioProperties.bucket)
                .build()
        }

    private fun probeUploadAndDelete(): String {
        minioClient.putObject(
            PutObjectArgs
                .builder()
                .bucket(minioProperties.bucket)
                .`object`(HEALTH_CHECK_FILE_NAME)
                .stream(ByteArrayInputStream(HEALTH_CHECK_PAYLOAD), HEALTH_CHECK_PAYLOAD.size.toLong(), -1)
                .contentType("text/plain")
                .build(),
        )
        minioClient.removeObject(
            RemoveObjectArgs
                .builder()
                .bucket(minioProperties.bucket)
                .`object`(HEALTH_CHECK_FILE_NAME)
                .build(),
        )
        return HEALTH_CHECK_FILE_NAME
    }

    companion object {
        private const val HEALTH_CHECK_FILE_NAME = "_healthcheck/probe.txt"
        private val HEALTH_CHECK_PAYLOAD = "ok".toByteArray()
    }
}
