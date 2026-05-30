package com.ark.base.user

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@SQLRestriction("deleted_at IS NULL")
class User(
    val email: String,
    val name: String,
    var passwordHash: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) {
    var deletedAt: LocalDateTime? = null
        protected set

    init {
        if (email.isBlank() || !email.matches(EMAIL_REGEX)) throw BaseException(ErrorCode.USER_INVALID_EMAIL)
        if (name.isBlank()) throw BaseException(ErrorCode.USER_BLANK_NAME)
        if (name.length > MAX_NAME_LENGTH) throw BaseException(ErrorCode.USER_NAME_TOO_LONG)
    }

    fun delete() {
        deletedAt = LocalDateTime.now()
    }

    fun changePassword(newPasswordHash: String) {
        passwordHash = newPasswordHash
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        private const val MAX_NAME_LENGTH = 100
    }
}
