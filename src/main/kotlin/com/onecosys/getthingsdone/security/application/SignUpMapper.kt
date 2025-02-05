package com.onecosys.getthingsdone.security.application

import com.onecosys.getthingsdone.dto.RegisterRequest
import com.onecosys.getthingsdone.user.domain.AppUser
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