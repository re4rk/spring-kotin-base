package com.ark.base.user

import com.ark.base.common.BaseAggregateEntity
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
    password: String,
    passwordEncoder: PasswordEncoder,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseAggregateEntity<User>() {
    var passwordHash: String = passwordEncoder.encode(password)

    init {
        validateEmail(email)
        validateName(name)
        registerEvent(UserRegisteredEvent(this))
    }

    fun delete(by: String? = null) {
        deletedAt = LocalDateTime.now()
        deletedBy = by
        isDeleted = true
    }

    fun matchesPassword(
        rawPassword: String,
        passwordEncoder: PasswordEncoder,
    ): Boolean = passwordEncoder.matches(rawPassword, passwordHash)

    fun changePassword(
        newPassword: String,
        passwordEncoder: PasswordEncoder,
    ) {
        passwordHash = passwordEncoder.encode(newPassword)
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        private const val MAX_NAME_LENGTH = 100

        private fun validateEmail(email: String) {
            if (email.isBlank() || !email.matches(EMAIL_REGEX)) throw BaseException(ErrorCode.USER_INVALID_EMAIL)
        }

        private fun validateName(name: String) {
            if (name.isBlank()) throw BaseException(ErrorCode.USER_BLANK_NAME)
            if (name.length > MAX_NAME_LENGTH) throw BaseException(ErrorCode.USER_NAME_TOO_LONG)
        }
    }
}
