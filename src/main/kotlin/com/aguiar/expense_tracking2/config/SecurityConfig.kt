package com.aguiar.expense_tracking2.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf{ it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/auth/**").permitAll() // registro e login públicos
                    .anyRequest().permitAll() // por enquanto, tudo público (depois mudamos para JWT)
            }
        return http.build()
    }

}