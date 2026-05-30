package com.ark.base.auth

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode

sealed class AuthException(
    errorCode: ErrorCode,
) : BaseException(errorCode) {
    class ResetTokenInvalid : AuthException(ErrorCode.USER_RESET_TOKEN_INVALID)
}
