package com.ark.base.application

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.file.FileClient
import com.ark.base.file.FileMetadataRepository
import com.ark.base.file.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class FileService(
    private val fileClient: FileClient,
    private val fileMetadataRepository: FileMetadataRepository,
) {
    @Transactional
    fun upload(request: FileRequest): FileResponse {
        validate(request.file)
        val stored =
            runCatching { fileClient.upload(request.toFileUpload()) }
                .getOrElse { throw BaseException(ErrorCode.FILE_UPLOAD_FAILED) }

        val metadata = fileMetadataRepository.save(request.toFileMetadata(stored))
        return FileResponse.from(metadata, stored.url)
    }

    @Transactional(readOnly = true)
    fun findById(fileId: Long): FileResponse {
        val metadata = fileMetadataRepository.findByIdOrThrow(fileId)
        val url = fileClient.getUrl(metadata.path)

        return FileResponse.from(metadata, url)
    }

    @Transactional
    fun delete(fileId: Long) {
        val metadata = fileMetadataRepository.findByIdOrThrow(fileId)

        metadata.delete()
        fileClient.delete(metadata.path)
    }

    private fun validate(file: MultipartFile) {
        if (file.isEmpty) throw BaseException(ErrorCode.FILE_EMPTY)
        if (file.contentType !in ALLOWED_MIME_TYPES) throw BaseException(ErrorCode.FILE_INVALID_TYPE)
    }

    companion object {
        private val ALLOWED_MIME_TYPES = setOf("image/jpeg", "image/png", "image/gif", "image/webp")
    }
}
