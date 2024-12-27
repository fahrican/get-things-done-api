package com.onecosys.getthingsdone.config

import com.onecosys.getthingsdone.user.repository.AppUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AuthenticationConfig(private val repository: AppUserRepository) {

    @Bean
    fun getUsername() =
        UserDetailsService { username ->
            repository.findByAppUsername(username) ?: throw UsernameNotFoundException("User not found")
        }

    @Bean
    fun getPasswordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun getAuthenticationProvider(): AuthenticationProvider {
        return DaoAuthenticationProvider().apply {
            this.setUserDetailsService(getUsername())
            this.setPasswordEncoder(getPasswordEncoder())
        }
    }

    @Bean
    fun getAuthenticationManager(
        authConfig: AuthenticationConfiguration
    ): AuthenticationManager = authConfig.authenticationManager
}