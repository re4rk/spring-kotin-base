package com.ark.base.auth.password.reset

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.data.repository.CrudRepository

interface PasswordResetTokenRepository : CrudRepository<PasswordResetToken, String>

fun PasswordResetTokenRepository.findByIdOrThrow(id: String) =
    findById(id).orElse(null) ?: throw BaseException(ErrorCode.USER_RESET_TOKEN_INVALID)
