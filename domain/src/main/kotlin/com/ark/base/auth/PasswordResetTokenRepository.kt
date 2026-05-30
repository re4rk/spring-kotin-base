package com.ark.base.auth

import org.springframework.data.repository.CrudRepository

interface PasswordResetTokenRepository : CrudRepository<PasswordResetToken, String>
