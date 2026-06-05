package com.ark.base.file

import java.io.InputStream

data class FileUpload(
    val originalName: String,
    val contentType: String,
    val size: Long,
    val inputStream: InputStream,
)
