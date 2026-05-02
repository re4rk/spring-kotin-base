package com.ark.base.user

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode

sealed class UserException(
    errorCode: ErrorCode,
) : BaseException(errorCode) {
    class InvalidEmail : UserException(ErrorCode.USER_INVALID_EMAIL)

    class BlankName : UserException(ErrorCode.USER_BLANK_NAME)

    class NameTooLong : UserException(ErrorCode.USER_NAME_TOO_LONG)

    class DuplicateEmail : UserException(ErrorCode.USER_DUPLICATE_EMAIL)

    class LoginFailed : UserException(ErrorCode.USER_LOGIN_FAILED)
}
