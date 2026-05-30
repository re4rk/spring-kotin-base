package com.ark.base.user

class UserRegisteredEvent(private val user: User) {
    val userId get() = user.id
    val email get() = user.email
    val name get() = user.name
}
