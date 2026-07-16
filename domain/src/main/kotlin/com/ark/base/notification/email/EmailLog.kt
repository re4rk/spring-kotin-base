package com.ark.base.notification.email

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
@Table(name = "base_email_log")
class EmailLog(
    @Column(nullable = false)
    val receiverEmail: String,
    @Column(nullable = false)
    val senderEmail: String,
    @Column(nullable = false)
    val subject: String,
    val templateId: String? = null,
    @Column(columnDefinition = "JSON")
    val templateParams: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: EmailStatus = EmailStatus.PENDING,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseEntity() {
    @Column(columnDefinition = "TEXT")
    var errorMessage: String? = null

    var messageId: String? = null

    var sentAt: LocalDateTime? = null

    fun markSuccess(messageId: String?) {
        status = EmailStatus.SUCCESS
        this.messageId = messageId
        sentAt = LocalDateTime.now()
    }

    fun markFailed(errorMessage: String) {
        status = EmailStatus.FAILED
        this.errorMessage = errorMessage
    }
}
