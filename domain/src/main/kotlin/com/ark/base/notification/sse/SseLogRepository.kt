package com.ark.base.notification.sse

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SseLogRepository : JpaRepository<SseLog, Long> {
    @Query("SELECT l.status, COUNT(l) FROM SseLog l GROUP BY l.status")
    fun countGroupedByStatus(): List<Array<Any>>
}
