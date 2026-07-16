package com.ark.base.notification.sms

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SmsLogRepository : JpaRepository<SmsLog, Long> {
    @Query("SELECT l.status, COUNT(l) FROM SmsLog l GROUP BY l.status")
    fun countGroupedByStatus(): List<Array<Any>>
}

