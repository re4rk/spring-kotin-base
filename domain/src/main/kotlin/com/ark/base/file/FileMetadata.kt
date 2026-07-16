package com.ark.base.file

import com.ark.base.common.BaseAggregateEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "base_file_metadata")
@SQLRestriction("deleted_at IS NULL")
class FileMetadata(
    val originalName: String,
    val storedName: String,
    val mimeType: String,
    val size: Long,
    val bucket: String,
    val path: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: FileUploadStatus = FileUploadStatus.PENDING,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseAggregateEntity<FileMetadata>() {
    @Column(columnDefinition = "TEXT")
    var errorMessage: String? = null

    fun markSuccess() {
        status = FileUploadStatus.SUCCESS
    }

    fun markFailed(errorMessage: String) {
        status = FileUploadStatus.FAILED
        this.errorMessage = errorMessage
    }

    fun delete(by: String? = null) {
        deletedAt = LocalDateTime.now()
        deletedBy = by
        isDeleted = true
    }
}
