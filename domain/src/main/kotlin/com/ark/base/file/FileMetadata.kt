package com.ark.base.file

import com.ark.base.common.BaseAggregateEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@SQLRestriction("deleted_at IS NULL")
class FileMetadata(
    val originalName: String,
    val storedName: String,
    val mimeType: String,
    val size: Long,
    val bucket: String,
    val path: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseAggregateEntity<FileMetadata>() {
    fun delete(by: String? = null) {
        deletedAt = LocalDateTime.now()
        deletedBy = by
        isDeleted = true
    }
}
