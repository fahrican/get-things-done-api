package com.onecosys.getthingsdone.auth.application

import com.onecosys.getthingsdone.auth.domain.AppUser
import com.onecosys.getthingsdone.dto.RegisterRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class SignUpMapper {

    fun toEntity(request: RegisterRequest, passwordEncoder: PasswordEncoder) = AppUser().apply {
        firstName = request.firstName
        lastName = request.lastName
        email = request.email
        appUsername = request.username
        appPassword = passwordEncoder.encode(request.password)
    }
}