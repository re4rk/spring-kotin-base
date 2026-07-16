package com.ark.base.config

import com.ark.base.user.PasswordEncoder
import com.ark.base.user.User
import com.ark.base.user.UserRepository
import com.ark.base.user.UserRole
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Profile("local")
@Component
@Order(1)
class LocalUserDataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : ApplicationRunner {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun run(args: ApplicationArguments) {
        if (userRepository.count() > 0) {
            log.info("[Seed] 유저 데이터 이미 존재 — skip")
            return
        }
        val admin =
            User(email = "admin@test.com", name = "관리자", password = "password123!", passwordEncoder = passwordEncoder)
                .also { it.role = UserRole.ADMIN }
        val users =
            listOf(
                admin,
                User(email = "user1@test.com", name = "테스트유저1", password = "password123!", passwordEncoder = passwordEncoder),
                User(email = "user2@test.com", name = "테스트유저2", password = "password123!", passwordEncoder = passwordEncoder),
            )
        userRepository.saveAll(users)
        log.info("[Seed] 유저 {}명 생성 완료", users.size)
    }
}
