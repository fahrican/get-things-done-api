package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.dto.AuthenticationRequest
import com.onecosys.getthingsdone.authentication.dto.AuthenticationResponse
import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.authorization.Role
import com.onecosys.getthingsdone.authorization.User
import com.onecosys.getthingsdone.authorization.UserRepository
import com.onecosys.getthingsdone.config.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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
        val user = User()
        user.firstName = request.firstName
        user.lastName = request.lastName
        user.email = request.email
        user.userPassword = passwordEncoder.encode(request.password)
        user.role = Role.USER

        repository.save(user)
        val jwtToken = jwtService.generateToken(user)
        return AuthenticationResponse(jwtToken)
    }

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.email, request.password))
        val user: User? = repository.findByEmail(request.email)
        var jwToken = ""
        user?.let { jwToken = jwtService.generateToken(user) }
        return  AuthenticationResponse(jwToken)
    }
}