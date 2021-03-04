package com.dorotatomczak.oauth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class PasswordEncodingConfig {
    @Bean("bcryptPasswordEncoder")
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(12)
}
