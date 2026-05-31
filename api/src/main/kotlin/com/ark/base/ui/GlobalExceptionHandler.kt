package com.ark.base.ui

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.ui.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ObjectOptimisticLockingFailureException::class)
    fun handleOptimisticLock(e: ObjectOptimisticLockingFailureException): ResponseEntity<ApiResponse> {
        log.warn("Optimistic lock failure", e)
        return ResponseEntity(
            ApiResponse.Error(
                code = ErrorCode.STOCK_CONCURRENT_MODIFICATION.name,
                message = ErrorCode.STOCK_CONCURRENT_MODIFICATION.message,
            ),
            HttpStatus.CONFLICT,
        )
    }

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
            ErrorCode.PRODUCT_NOT_FOUND,
            ErrorCode.PRODUCT_OPTION_GROUP_NOT_FOUND,
            ErrorCode.PRODUCT_OPTION_NOT_FOUND,
            ErrorCode.PRODUCT_SKU_NOT_FOUND,
            ErrorCode.ORDER_NOT_FOUND,
            ErrorCode.FILE_NOT_FOUND,
            -> HttpStatus.NOT_FOUND

            ErrorCode.INVALID_INPUT,
            ErrorCode.USER_INVALID_EMAIL,
            ErrorCode.USER_BLANK_NAME,
            ErrorCode.USER_NAME_TOO_LONG,
            ErrorCode.ORDER_INVALID_STATUS,
            ErrorCode.PRODUCT_INVALID_STATUS,
            ErrorCode.FILE_INVALID_TYPE,
            ErrorCode.FILE_EMPTY,
            -> HttpStatus.BAD_REQUEST

            ErrorCode.USER_DUPLICATE_EMAIL,
            ErrorCode.STOCK_INSUFFICIENT,
            ErrorCode.STOCK_CONCURRENT_MODIFICATION,
            ErrorCode.ORDER_ALREADY_CANCELLED,
            ErrorCode.PRODUCT_OPTION_IN_USE,
            -> HttpStatus.CONFLICT

            ErrorCode.USER_LOGIN_FAILED,
            ErrorCode.UNAUTHORIZED,
            -> HttpStatus.UNAUTHORIZED

            ErrorCode.ACCESS_DENIED -> HttpStatus.FORBIDDEN

            ErrorCode.USER_RESET_TOKEN_INVALID,
            ErrorCode.USER_REFRESH_TOKEN_INVALID,
            ErrorCode.USER_REFRESH_TOKEN_REUSED,
            -> HttpStatus.BAD_REQUEST

            ErrorCode.FILE_UPLOAD_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR
        }
}
