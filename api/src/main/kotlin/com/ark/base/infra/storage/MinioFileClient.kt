package com.ark.base.infra.storage

import com.ark.base.common.MinioProperties
import com.ark.base.file.FileClient
import com.ark.base.file.FileUpload
import com.ark.base.file.StoredFile
import io.minio.BucketExistsArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.http.Method
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.TimeUnit

@Component
class MinioFileClient(
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties,
) : FileClient {
    private val log = LoggerFactory.getLogger(javaClass)

    init {
        ensureBucketExists()
    }

    override fun upload(upload: FileUpload): StoredFile {
        val extension = upload.originalName.substringAfterLast('.', "")
        val storedName = if (extension.isNotEmpty()) "${UUID.randomUUID()}.$extension" else "${UUID.randomUUID()}"

        log.info("Uploading file originalName={} storedName={} size={}", upload.originalName, storedName, upload.size)
        minioClient.putObject(
            PutObjectArgs
                .builder()
                .bucket(minioProperties.bucket)
                .`object`(storedName)
                .stream(upload.inputStream, upload.size, -1)
                .contentType(upload.contentType)
                .build(),
        )
        log.info("Uploaded file storedName={} bucket={}", storedName, minioProperties.bucket)

        return StoredFile(
            storedName = storedName,
            bucket = minioProperties.bucket,
            path = storedName,
            url = getUrl(storedName),
        )
    }

    override fun delete(path: String) {
        log.info("Deleting file path={} bucket={}", path, minioProperties.bucket)
        minioClient.removeObject(
            RemoveObjectArgs
                .builder()
                .bucket(minioProperties.bucket)
                .`object`(path)
                .build(),
        )
        log.info("Deleted file path={}", path)
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
            log.info("Bucket not found, creating bucket={}", minioProperties.bucket)
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.bucket).build())
            log.info("Bucket created bucket={}", minioProperties.bucket)
        }
    }
}
