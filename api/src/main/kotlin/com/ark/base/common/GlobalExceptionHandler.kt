package com.ark.base.common

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ApiResponse> =
        ResponseEntity(
            ApiResponse.Error(code = e.errorCode.name, message = e.errorCode.message),
            e.errorCode.toHttpStatus(),
        )

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse> {
        log.error("Unhandled exception", e)
        return ResponseEntity(
            ApiResponse.Error(code = "S001", message = "서버 내부 오류가 발생했습니다."),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }

    private fun ErrorCode.toHttpStatus(): HttpStatus =
        when (this) {
            ErrorCode.NOT_FOUND,
            ErrorCode.USER_NOT_FOUND,
            ErrorCode.ORDER_NOT_FOUND,
            ErrorCode.INVENTORY_NOT_FOUND,
            -> HttpStatus.NOT_FOUND

            ErrorCode.INVALID_INPUT,
            ErrorCode.USER_INVALID_EMAIL,
            ErrorCode.USER_BLANK_NAME,
            ErrorCode.USER_NAME_TOO_LONG,
            -> HttpStatus.BAD_REQUEST

            ErrorCode.USER_DUPLICATE_EMAIL,
            ErrorCode.STOCK_INSUFFICIENT,
            -> HttpStatus.CONFLICT

            ErrorCode.USER_LOGIN_FAILED -> HttpStatus.UNAUTHORIZED
            ErrorCode.USER_RESET_TOKEN_INVALID -> HttpStatus.BAD_REQUEST
        }
}
