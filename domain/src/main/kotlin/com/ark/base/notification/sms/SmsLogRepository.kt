package com.ark.base.notification.sms

import org.springframework.data.jpa.repository.JpaRepository

interface SmsLogRepository : JpaRepository<SmsLog, Long>
