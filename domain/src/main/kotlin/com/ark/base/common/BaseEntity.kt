package com.ark.base.common

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.MIN
        protected set

    @CreatedBy
    var createdBy: String? = null
        protected set

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.MIN
        protected set

    @LastModifiedBy
    var updatedBy: String? = null
        protected set

    var deletedAt: LocalDateTime? = null
        protected set

    var deletedBy: String? = null
        protected set

    var isDeleted: Boolean = false
        protected set
}
