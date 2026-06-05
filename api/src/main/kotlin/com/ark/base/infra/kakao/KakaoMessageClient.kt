package com.ark.base.infra.kakao

import com.ark.base.notification.kakao.KakaoClient
import com.ark.base.notification.kakao.KakaoLog
import com.ark.base.notification.kakao.KakaoLogRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class KakaoMessageClient(
    private val kakaoLogRepository: KakaoLogRepository,
) : KakaoClient {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(
        to: String,
        message: String,
    ) {
        log.info("Sending kakao to={} message={}", to, message)
        val kakaoLog =
            kakaoLogRepository.save(
                KakaoLog(recipient = to, message = message),
            )
        try {
            // TODO: 카카오 알림톡 API 연동
            kakaoLog.markSuccess(null)
            log.info("Kakao sent to={}", to)
        } catch (e: Exception) {
            kakaoLog.markFailed(e.message ?: e.javaClass.simpleName)
            log.error("Failed to send kakao to={} error={}", to, e.message, e)
            throw e
        } finally {
            kakaoLogRepository.save(kakaoLog)
        }
    }
}
