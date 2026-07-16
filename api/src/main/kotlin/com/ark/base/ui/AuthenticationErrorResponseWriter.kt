package com.ark.base.ui

import com.ark.base.common.ErrorCode
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import java.nio.charset.StandardCharsets

@Component
class AuthenticationErrorResponseWriter(
    private val jsonMapper: JsonMapper,
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
        response.writer.write(jsonMapper.writeValueAsString(body))
    }
}
