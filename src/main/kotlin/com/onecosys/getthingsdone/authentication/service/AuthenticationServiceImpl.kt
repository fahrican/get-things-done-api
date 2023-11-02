package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.dto.AuthenticationRequest
import com.onecosys.getthingsdone.authentication.dto.AuthenticationResponse
import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.authentication.util.UserRegistrationMapper
import com.onecosys.getthingsdone.authorization.TokenRepository
import com.onecosys.getthingsdone.authorization.model.Token
import com.onecosys.getthingsdone.error.SignUpException
import com.onecosys.getthingsdone.error.UsernamePasswordMismatchException
import com.onecosys.getthingsdone.user.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceImpl(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val mapper: UserRegistrationMapper
) : AuthenticationService {

    @Transactional
    override fun signUp(request: RegisterRequest): AuthenticationResponse {

        checkForSignUpMistakes(request)

        val user = mapper.toEntity(request, passwordEncoder)

        val savedUser = userRepository.save(user)
        val jwtToken = jwtService.generateAccessToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)
        saveUserToken(savedUser, jwtToken)

        return AuthenticationResponse(jwtToken, refreshToken)
    }

    private fun checkForSignUpMistakes(request: RegisterRequest) {
        userRepository.findByEmail(request.email)?.let {
            throw SignUpException("User email already exists!")
        }

        userRepository.findBy_username(request.username)?.let {
            throw SignUpException("Username already exists!")
        }

        if (request.password != request.passwordConfirmation) {
            throw SignUpException("Password and password confirmation does not match!")
        }
    }

    @Transactional
    override fun signIn(request: AuthenticationRequest): AuthenticationResponse {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))
        } catch (e: BadCredentialsException) {
            throw UsernamePasswordMismatchException("Username or password is incorrect")
        }

        val user = userRepository.findBy_username(request.username) ?: throw UsernameNotFoundException("User not found")

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
