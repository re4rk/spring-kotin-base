package com.ark.base.file

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FileMetadataRepository : JpaRepository<FileMetadata, Long> {
    @Query("SELECT f FROM FileMetadata f WHERE f.id = ?1 AND f.isDeleted = false")
    fun findActiveById(id: Long): FileMetadata?
}

fun FileMetadataRepository.getById(id: Long): FileMetadata =
    findActiveById(id) ?: throw BaseException(ErrorCode.FILE_NOT_FOUND)
