package com.ark.base.notification.slack

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SlackLogRepository : JpaRepository<SlackLog, Long> {
    @Query("SELECT l.status, COUNT(l) FROM SlackLog l GROUP BY l.status")
    fun countGroupedByStatus(): List<Array<Any>>
}
