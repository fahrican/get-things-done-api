package com.onecosys.getthingsdone.authentication.util

import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.user.entity.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserRegistrationMapper {

    fun toEntity(request: RegisterRequest, passwordEncoder: PasswordEncoder) = User().apply {
        firstName = request.firstName
        lastName = request.lastName
        email = request.email
        _username = request.username
        _password = passwordEncoder.encode(request.password)
    }
}