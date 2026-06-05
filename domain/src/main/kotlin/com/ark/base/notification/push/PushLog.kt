package com.ark.base.notification.push

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
@Table(name = "push_log")
class PushLog(
    @Column(nullable = false)
    val recipient: String,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    val body: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: PushStatus = PushStatus.PENDING,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    @Column(columnDefinition = "TEXT")
    var errorMessage: String? = null

    var messageId: String? = null

    var sentAt: LocalDateTime? = null

    fun markSuccess(messageId: String?) {
        status = PushStatus.SUCCESS
        this.messageId = messageId
        sentAt = LocalDateTime.now()
    }

    fun markFailed(errorMessage: String) {
        status = PushStatus.FAILED
        this.errorMessage = errorMessage
    }
}

enum class PushStatus {
    PENDING,
    SUCCESS,
    FAILED,
}
