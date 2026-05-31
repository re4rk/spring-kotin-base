package com.ark.base.application

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.file.FileMetadata
import com.ark.base.file.FileMetadataRepository
import com.ark.base.file.FileStorage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class FileService(
    private val fileStorage: FileStorage,
    private val fileMetadataRepository: FileMetadataRepository,
) {
    @Transactional
    fun upload(request: FileRequest): FileResponse {
        val file = request.file
        validate(file)
        val stored =
            try {
                fileStorage.upload(
                    originalName = file.originalFilename ?: "unknown",
                    contentType = file.contentType!!,
                    size = file.size,
                    inputStream = file.inputStream,
                )
            } catch (e: Exception) {
                throw BaseException(ErrorCode.FILE_UPLOAD_FAILED)
            }
        val metadata =
            fileMetadataRepository.save(
                FileMetadata(
                    originalName = file.originalFilename ?: "unknown",
                    storedName = stored.storedName,
                    mimeType = file.contentType!!,
                    size = file.size,
                    bucket = stored.bucket,
                    path = stored.path,
                ),
            )
        return FileResponse.from(metadata, stored.url)
    }

    @Transactional(readOnly = true)
    fun findById(fileId: Long): FileResponse {
        val metadata = fileMetadataRepository.findActiveById(fileId) ?: throw BaseException(ErrorCode.FILE_NOT_FOUND)
        return FileResponse.from(metadata, fileStorage.getUrl(metadata.path))
    }

    @Transactional
    fun delete(fileId: Long) {
        val metadata = fileMetadataRepository.getById(fileId)
        metadata.delete()
        fileStorage.delete(metadata.path)
    }

    private fun validate(file: MultipartFile) {
        if (file.isEmpty) throw BaseException(ErrorCode.FILE_EMPTY)
        if (file.contentType !in ALLOWED_MIME_TYPES) throw BaseException(ErrorCode.FILE_INVALID_TYPE)
    }

    companion object {
        private val ALLOWED_MIME_TYPES = setOf("image/jpeg", "image/png", "image/gif", "image/webp")
    }
}
