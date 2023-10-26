package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.dto.AuthenticationRequest
import com.onecosys.getthingsdone.authentication.dto.AuthenticationResponse
import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.authentication.error.SignUpException
import com.onecosys.getthingsdone.authorization.TokenRepository
import com.onecosys.getthingsdone.user.User
import com.onecosys.getthingsdone.user.UserRepository
import com.onecosys.getthingsdone.authorization.model.Token
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    fun signUp(request: RegisterRequest): AuthenticationResponse {
        userRepository.findByEmail(request.email)?.let {
            throw SignUpException("User email already exists!")
        }

        val user = User().apply {
            firstName = request.firstName
            lastName = request.lastName
            email = request.email
            userPassword = passwordEncoder.encode(request.password)
            role = request.role
        }

        val savedUser = userRepository.save(user)
        val jwtToken = jwtService.generateAccessToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)
        saveUserToken(savedUser, jwtToken)

        return AuthenticationResponse(jwtToken, refreshToken)
    }

    fun signIn(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.email, request.password))
        val user: User = userRepository.findByEmail(request.email) ?: throw UsernameNotFoundException("User not found")
        val jwtToken = jwtService.generateAccessToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)
        revokeAllUserTokens(user)
        saveUserToken(user, jwtToken)
        return AuthenticationResponse(jwtToken, refreshToken)
    }

    private fun saveUserToken(user: User, jwtToken: String) {
        val token = Token().apply {
            this.user = user
            this.token = jwtToken
            this.expired = false
            this.revoked = false
        }
        tokenRepository.save(token)
    }

    private fun revokeAllUserTokens(user: User) {
        val validUserTokens = tokenRepository.findAllValidTokenByUser(user.id)
        validUserTokens?.let { userTokens ->
            if (userTokens.isEmpty()) return
            userTokens.forEach { token ->
                token?.expired = true
                token?.revoked = true
            }
            tokenRepository.saveAll(validUserTokens)
        }
    }
}
