package com.ark.base.notification.sse

import com.ark.base.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "base_sse_log")
class SseLog(
    @Column(nullable = false)
    val userId: Long,
    @Column(nullable = false)
    val eventType: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    val data: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: SseStatus = SseStatus.PENDING,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    @Column(columnDefinition = "TEXT")
    var errorMessage: String? = null

    var sentAt: LocalDateTime? = null

    fun markDelivered() {
        status = SseStatus.DELIVERED
        sentAt = LocalDateTime.now()
    }

    fun markMissed() {
        status = SseStatus.MISSED
    }

    fun markFailed(errorMessage: String) {
        status = SseStatus.FAILED
        this.errorMessage = errorMessage
    }
}
