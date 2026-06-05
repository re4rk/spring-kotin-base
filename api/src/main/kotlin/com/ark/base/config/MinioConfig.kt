package com.ark.base.config

import io.minio.MinioClient
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(MinioProperties::class)
class MinioConfig(
    private val properties: MinioProperties,
) {
    @Bean
    fun minioClient(): MinioClient =
        MinioClient
            .builder()
            .endpoint(properties.endpoint)
            .credentials(properties.accessKey, properties.secretKey)
            .build()
}
