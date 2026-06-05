package com.ark.base.notification.slack

import org.springframework.data.jpa.repository.JpaRepository

interface SlackLogRepository : JpaRepository<SlackLog, Long>
