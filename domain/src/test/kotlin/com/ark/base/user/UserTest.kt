package com.ark.base.user

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.support.FakePasswordEncoder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserTest {
    private val passwordEncoder = FakePasswordEncoder()

    @Test
    fun `유효한 정보로 사용자를 생성할 수 있다`() {
        val user = user()

        assertEquals("user@test.com", user.email)
        assertEquals("홍길동", user.name)
        assertEquals("encoded:password123", user.passwordHash)
    }

    @Test
    fun `이메일 형식이 잘못되면 USER_INVALID_EMAIL 예외를 던진다`() {
        val exception =
            assertThrows<BaseException> {
                User(email = "invalid-email", name = "홍길동", password = "secret", passwordEncoder = passwordEncoder)
            }

        assertEquals(ErrorCode.USER_INVALID_EMAIL, exception.errorCode)
    }

    @Test
    fun `이름이 비어 있으면 USER_BLANK_NAME 예외를 던진다`() {
        val exception =
            assertThrows<BaseException> {
                User(email = "user@test.com", name = "  ", password = "secret", passwordEncoder = passwordEncoder)
            }

        assertEquals(ErrorCode.USER_BLANK_NAME, exception.errorCode)
    }

    @Test
    fun `이름이 100자를 초과하면 USER_NAME_TOO_LONG 예외를 던진다`() {
        val exception =
            assertThrows<BaseException> {
                User(email = "user@test.com", name = "a".repeat(101), password = "secret", passwordEncoder = passwordEncoder)
            }

        assertEquals(ErrorCode.USER_NAME_TOO_LONG, exception.errorCode)
    }

    @Test
    fun `비밀번호 일치 여부를 확인할 수 있다`() {
        val user = user()

        assertTrue(user.matchesPassword("password123", passwordEncoder))
    }

    @Test
    fun `비밀번호를 변경할 수 있다`() {
        val user = user()

        user.changePassword("new-password", passwordEncoder)

        assertTrue(user.matchesPassword("new-password", passwordEncoder))
    }

    @Test
    fun `사용자를 삭제하면 soft delete 상태가 된다`() {
        val user = user()

        user.delete(by = "1")

        assertTrue(user.isDeleted)
        assertEquals("1", user.deletedBy)
    }

    private fun user() =
        User(
            email = "user@test.com",
            name = "홍길동",
            password = "password123",
            passwordEncoder = passwordEncoder,
        )
}
