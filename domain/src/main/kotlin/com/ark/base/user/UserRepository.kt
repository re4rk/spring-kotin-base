package com.ark.base.user

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}

fun UserRepository.getById(id: Long): User = findById(id).orElseThrow { BaseException(ErrorCode.USER_NOT_FOUND) }
