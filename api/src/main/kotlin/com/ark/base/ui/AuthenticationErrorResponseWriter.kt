package com.ark.base.ui

import com.ark.base.common.ErrorCode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import java.nio.charset.StandardCharsets
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

@Component
class AuthenticationErrorResponseWriter(
    private val objectMapper: ObjectMapper,
) {
    fun write(
        response: HttpServletResponse,
        status: Int,
        errorCode: ErrorCode,
    ) {
        response.status = status
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.contentType = "${MediaType.APPLICATION_JSON_VALUE};charset=UTF-8"
        val body =
            ApiResponse.Error(
                code = errorCode.name,
                message = errorCode.message,
            )
        response.writer.write(objectMapper.writeValueAsString(body))
    }
}
