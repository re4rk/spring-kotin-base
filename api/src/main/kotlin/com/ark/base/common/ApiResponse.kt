package com.ark.base.common

sealed interface ApiResponse {
    data class Success<T>(
        val data: T,
    ) : ApiResponse

    data class Error(
        val code: String,
        val message: String,
    ) : ApiResponse
}
