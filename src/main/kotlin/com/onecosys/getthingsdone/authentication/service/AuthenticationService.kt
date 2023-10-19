package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.dto.AuthenticationRequest
import com.onecosys.getthingsdone.authentication.dto.AuthenticationResponse
import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.authentication.error.SignUpException
import com.onecosys.getthingsdone.authorization.Role
import com.onecosys.getthingsdone.authorization.User
import com.onecosys.getthingsdone.authorization.UserRepository
import com.onecosys.getthingsdone.config.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    fun register(request: RegisterRequest): AuthenticationResponse {
        repository.findByEmail(request.email)?.let {
            throw SignUpException("User email already exists!")
        }

        val user = User().apply {
            firstName = request.firstName
            lastName = request.lastName
            email = request.email
            userPassword = passwordEncoder.encode(request.password)
            role = Role.USER
        }

        repository.save(user)
        val jwtToken = jwtService.generateToken(user)
        return AuthenticationResponse(jwtToken)
    }

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.email, request.password))
        val user: User = repository.findByEmail(request.email) ?: throw UsernameNotFoundException("User not found")
        val jwtToken = jwtService.generateToken(user)
        return AuthenticationResponse(jwtToken)
    }
}
