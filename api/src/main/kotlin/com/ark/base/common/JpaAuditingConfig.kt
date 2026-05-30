package com.ark.base.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional

@Configuration
@EnableJpaAuditing
class JpaAuditingConfig {
    @Bean
    fun auditorAware(): AuditorAware<String> =
        AuditorAware {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication == null || !authentication.isAuthenticated) {
                Optional.empty()
            } else {
                Optional.ofNullable(authentication.name)
            }
        }
}
