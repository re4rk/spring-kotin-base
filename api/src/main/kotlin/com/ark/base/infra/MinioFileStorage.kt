package com.ark.base.infra

import com.ark.base.common.MinioProperties
import com.ark.base.file.FileStorage
import com.ark.base.file.StoredFile
import io.minio.BucketExistsArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.http.Method
import org.springframework.stereotype.Component
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

@Component
class MinioFileStorage(
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties,
) : FileStorage {
    init {
        ensureBucketExists()
    }

    override fun upload(
        originalName: String,
        contentType: String,
        size: Long,
        inputStream: InputStream,
    ): StoredFile {
        val extension = originalName.substringAfterLast('.', "")
        val storedName = if (extension.isNotEmpty()) "${UUID.randomUUID()}.$extension" else "${UUID.randomUUID()}"

        minioClient.putObject(
            PutObjectArgs
                .builder()
                .bucket(minioProperties.bucket)
                .`object`(storedName)
                .stream(inputStream, size, -1)
                .contentType(contentType)
                .build(),
        )

        return StoredFile(
            storedName = storedName,
            bucket = minioProperties.bucket,
            path = storedName,
            url = getUrl(storedName),
        )
    }

    override fun delete(path: String) {
        minioClient.removeObject(
            RemoveObjectArgs
                .builder()
                .bucket(minioProperties.bucket)
                .`object`(path)
                .build(),
        )
    }

    override fun getUrl(path: String): String =
        minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs
                .builder()
                .method(Method.GET)
                .bucket(minioProperties.bucket)
                .`object`(path)
                .expiry(7, TimeUnit.DAYS)
                .build(),
        )

    private fun ensureBucketExists() {
        val exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.bucket).build())
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.bucket).build())
        }
    }
}
