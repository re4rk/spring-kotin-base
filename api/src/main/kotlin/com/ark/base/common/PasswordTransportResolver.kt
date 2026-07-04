package com.ark.base.common

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.stereotype.Component

@Component
class PasswordTransportResolver(
    private val passwordTransportCrypto: PasswordTransportCrypto,
) {
    fun resolve(value: String): String {
        if (!value.startsWith(RSA_PREFIX)) return value
        return try {
            passwordTransportCrypto.decrypt(value.removePrefix(RSA_PREFIX))
        } catch (_: Exception) {
            throw BaseException(ErrorCode.INVALID_INPUT)
        }
    }

    companion object {
        const val RSA_PREFIX = "rsa:"
    }
}
