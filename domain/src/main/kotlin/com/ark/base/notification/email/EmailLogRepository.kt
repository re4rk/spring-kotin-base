package com.ark.base.notification.email

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface EmailLogRepository : JpaRepository<EmailLog, Long> {
    @Query("SELECT l.status, COUNT(l) FROM EmailLog l GROUP BY l.status")
    fun countGroupedByStatus(): List<Array<Any>>
}

