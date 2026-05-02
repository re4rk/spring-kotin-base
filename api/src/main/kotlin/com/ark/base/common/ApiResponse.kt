package com.ark.base.common

sealed interface ApiResponse

data class SuccessResponse<T>(
    val data: T,
) : ApiResponse

data class ErrorResponse(
    val code: String,
    val message: String,
) : ApiResponse
