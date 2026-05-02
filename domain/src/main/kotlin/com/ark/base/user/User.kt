package com.ark.base.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    val email: String,
    val name: String,
    var passwordHash: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) {
    init {
        if (email.isBlank() || !email.matches(EMAIL_REGEX)) throw UserException.InvalidEmail()
        if (name.isBlank()) throw UserException.BlankName()
        if (name.length > MAX_NAME_LENGTH) throw UserException.NameTooLong()
    }

    fun changePassword(newPasswordHash: String) {
        passwordHash = newPasswordHash
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        private const val MAX_NAME_LENGTH = 100
    }
}
