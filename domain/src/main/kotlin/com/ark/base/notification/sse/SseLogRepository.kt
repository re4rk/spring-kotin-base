package com.ark.base.notification.sse

import org.springframework.data.jpa.repository.JpaRepository

interface SseLogRepository : JpaRepository<SseLog, Long>
