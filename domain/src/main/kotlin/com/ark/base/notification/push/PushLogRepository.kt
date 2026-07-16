package com.ark.base.notification.push

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PushLogRepository : JpaRepository<PushLog, Long> {
    @Query("SELECT l.status, COUNT(l) FROM PushLog l GROUP BY l.status")
    fun countGroupedByStatus(): List<Array<Any>>
}
