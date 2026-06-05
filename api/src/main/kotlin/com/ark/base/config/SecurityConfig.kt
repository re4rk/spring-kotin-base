package com.ark.base.config

import com.ark.base.common.ErrorCode
import com.ark.base.ui.JwtAuthenticationFilter
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .cors { }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/actuator/health", "/actuator/health/**")
                    .permitAll()
                    .requestMatchers("/auth/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/products", "/products/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.exceptionHandling {
                it
                    .authenticationEntryPoint { _, response, _ ->
                        writeError(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorCode.UNAUTHORIZED)
                    }.accessDeniedHandler { _, response, _ ->
                        writeError(response, HttpServletResponse.SC_FORBIDDEN, ErrorCode.ACCESS_DENIED)
                    }
            }.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()

    private fun writeError(
        response: HttpServletResponse,
        status: Int,
        errorCode: ErrorCode,
    ) {
        response.status = status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(
            """{"code":"${errorCode.name}","message":"${errorCode.message}"}""",
        )
    }
}
