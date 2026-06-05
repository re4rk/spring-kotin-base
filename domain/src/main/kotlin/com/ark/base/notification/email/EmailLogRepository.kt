package com.ark.base.notification.email

import org.springframework.data.jpa.repository.JpaRepository

interface EmailLogRepository : JpaRepository<EmailLog, Long>
