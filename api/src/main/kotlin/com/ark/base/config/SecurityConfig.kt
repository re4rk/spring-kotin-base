package com.ark.base.config

import com.ark.base.common.ErrorCode
import com.ark.base.ui.AuthenticationErrorResponseWriter
import com.ark.base.ui.JwtAuthenticationFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.RequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val authenticationErrorResponseWriter: AuthenticationErrorResponseWriter,
) {
    @Bean
    fun springPasswordEncoder(): org.springframework.security.crypto.password.PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Order(1)
    fun adminSecurityFilterChain(
        http: HttpSecurity,
        adminUserDetailsService: UserDetailsService,
    ): SecurityFilterChain {
        val authProvider = DaoAuthenticationProvider(adminUserDetailsService)
        authProvider.setPasswordEncoder(springPasswordEncoder())

        val adminWebMatcher =
            RequestMatcher { req: HttpServletRequest ->
                req.requestURI.startsWith("/admin") && !req.requestURI.startsWith("/admin/api")
            }

        return http
            .securityMatcher(adminWebMatcher)
            .authenticationProvider(authProvider)
            .authorizeHttpRequests {
                it.requestMatchers("/admin/login", "/admin/oauth/**").permitAll()
                it.requestMatchers("/admin/**").hasRole("ADMIN")
            }.formLogin {
                it.loginPage("/admin/login")
                it.loginProcessingUrl("/admin/login")
                it.defaultSuccessUrl("/admin/users", true)
                it.failureUrl("/admin/login?error=true")
            }.logout {
                it.logoutUrl("/admin/logout")
                it.logoutSuccessUrl("/admin/login?logout=true")
            }.build()
    }

    @Bean
    @Order(2)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .cors { }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/actuator/health", "/actuator/health/**")
                    .permitAll()
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico")
                    .permitAll()
                    .requestMatchers("/auth/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/products", "/products/**")
                    .permitAll()
                    .requestMatchers("/admin/api/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated()
            }.exceptionHandling {
                it
                    .authenticationEntryPoint { _, response, _ ->
                        authenticationErrorResponseWriter.write(
                            response,
                            HttpServletResponse.SC_UNAUTHORIZED,
                            ErrorCode.UNAUTHORIZED,
                        )
                    }.accessDeniedHandler { _, response, _ ->
                        authenticationErrorResponseWriter.write(
                            response,
                            HttpServletResponse.SC_FORBIDDEN,
                            ErrorCode.ACCESS_DENIED,
                        )
                    }
            }.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
}
