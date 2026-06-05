package com.ark.base.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "minio")
data class MinioProperties(
    val endpoint: String = "http://localhost:9000",
    val accessKey: String = "minioadmin",
    val secretKey: String = "minioadmin",
    val bucket: String = "files",
)
