package com.ark.base.file

interface FileClient {
    fun upload(upload: FileUpload): FileMetadata

    fun delete(path: String)

    fun getUrl(path: String): String
}
