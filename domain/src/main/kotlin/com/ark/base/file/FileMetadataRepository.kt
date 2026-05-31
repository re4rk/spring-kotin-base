package com.ark.base.file

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.data.jpa.repository.JpaRepository

interface FileMetadataRepository : JpaRepository<FileMetadata, Long>

fun FileMetadataRepository.findByIdOrThrow(id: Long): FileMetadata =
    findById(id).orElseGet { null } ?: throw BaseException(ErrorCode.FILE_NOT_FOUND)
