package com.ark.base.notification.push

import org.springframework.data.jpa.repository.JpaRepository

interface PushLogRepository : JpaRepository<PushLog, Long>
