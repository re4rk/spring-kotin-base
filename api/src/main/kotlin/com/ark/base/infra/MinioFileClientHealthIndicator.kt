package com.ark.base.infra

import com.ark.base.common.MinioProperties
import com.ark.base.file.FileUpload
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
    private val minioFileClient: MinioFileClient,
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
        val stored = minioFileClient.upload(fileUpload)
        minioFileClient.delete(stored.path)
        return stored.path
    }

    companion object {
        private const val HEALTH_CHECK_FILE_NAME = "_healthcheck/probe.txt"
        private val HEALTH_CHECK_PAYLOAD = "ok".toByteArray()
        private val fileUpload =
            FileUpload(
                originalName = HEALTH_CHECK_FILE_NAME,
                contentType = "text/plain",
                size = HEALTH_CHECK_PAYLOAD.size.toLong(),
                inputStream = ByteArrayInputStream(HEALTH_CHECK_PAYLOAD),
            )
    }
}
