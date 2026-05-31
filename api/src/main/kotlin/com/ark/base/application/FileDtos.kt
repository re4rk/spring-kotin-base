package com.ark.base.application

import com.ark.base.file.FileMetadata
import com.ark.base.file.FileUpload
import com.ark.base.file.StoredFile
import org.springframework.web.multipart.MultipartFile

data class FileRequest(
    val file: MultipartFile,
) {
    fun toFileUpload() =
        FileUpload(
            originalName = file.originalFilename ?: "unknown",
            contentType = file.contentType!!,
            size = file.size,
            inputStream = file.inputStream,
        )

    fun toFileMetadata(stored: StoredFile): FileMetadata {
        val upload = toFileUpload()
        return FileMetadata(
            originalName = upload.originalName,
            storedName = stored.storedName,
            mimeType = upload.contentType,
            size = upload.size,
            bucket = stored.bucket,
            path = stored.path,
        )
    }
}

data class FileResponse(
    val id: Long,
    val originalName: String,
    val mimeType: String,
    val size: Long,
    val url: String,
) {
    companion object {
        fun from(
            metadata: FileMetadata,
            url: String,
        ) = FileResponse(
            id = metadata.id,
            originalName = metadata.originalName,
            mimeType = metadata.mimeType,
            size = metadata.size,
            url = url,
        )
    }
}
