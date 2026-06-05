package com.ark.base.notification.sms

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
@Table(name = "sms_log")
class SmsLog(
    @Column(nullable = false)
    val recipient: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    val message: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: SmsStatus = SmsStatus.PENDING,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    @Column(columnDefinition = "TEXT")
    var errorMessage: String? = null

    var messageId: String? = null

    var sentAt: LocalDateTime? = null

    fun markSuccess(messageId: String?) {
        status = SmsStatus.SUCCESS
        this.messageId = messageId
        sentAt = LocalDateTime.now()
    }

    fun markFailed(errorMessage: String) {
        status = SmsStatus.FAILED
        this.errorMessage = errorMessage
    }
}

