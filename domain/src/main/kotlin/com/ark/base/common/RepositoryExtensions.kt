package com.ark.base.common

import org.springframework.data.repository.CrudRepository

fun <T, ID> CrudRepository<T, ID>.getById(id: ID, errorCode: ErrorCode = ErrorCode.NOT_FOUND): T =
    findById(id).orElseThrow { BaseException(errorCode) }
