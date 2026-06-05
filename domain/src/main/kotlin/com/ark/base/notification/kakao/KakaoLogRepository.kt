package com.ark.base.notification.kakao

import org.springframework.data.jpa.repository.JpaRepository

interface KakaoLogRepository : JpaRepository<KakaoLog, Long>
