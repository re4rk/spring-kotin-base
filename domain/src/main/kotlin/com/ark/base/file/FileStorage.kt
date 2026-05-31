package com.ark.base.file

import java.io.InputStream

interface FileStorage {
    fun upload(
        originalName: String,
        contentType: String,
        size: Long,
        inputStream: InputStream,
    ): StoredFile

    fun delete(path: String)

    fun getUrl(path: String): String
}

data class StoredFile(
    val storedName: String,
    val bucket: String,
    val path: String,
    val url: String,
)
