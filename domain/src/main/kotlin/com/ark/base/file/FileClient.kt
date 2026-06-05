package com.ark.base.file

import java.io.InputStream

interface FileClient {
    fun upload(upload: FileUpload): FileMetadata

    fun delete(path: String)

    fun getUrl(path: String): String
}

data class FileUpload(
    val originalName: String,
    val contentType: String,
    val size: Long,
    val inputStream: InputStream,
)
