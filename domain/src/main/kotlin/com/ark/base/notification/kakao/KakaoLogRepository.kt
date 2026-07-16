package com.ark.base.notification.kakao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface KakaoLogRepository : JpaRepository<KakaoLog, Long> {
    @Query("SELECT l.status, COUNT(l) FROM KakaoLog l GROUP BY l.status")
    fun countGroupedByStatus(): List<Array<Any>>
}
