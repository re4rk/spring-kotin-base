package com.ark.base.auth.password.reset

import org.springframework.data.repository.CrudRepository

interface PasswordResetTokenRepository : CrudRepository<PasswordResetToken, String>
