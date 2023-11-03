package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.dto.AuthenticationRequest
import com.onecosys.getthingsdone.authentication.dto.AuthenticationResponse
import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.authentication.dto.VerificationToken
import com.onecosys.getthingsdone.authentication.repository.VerificationTokenRepository
import com.onecosys.getthingsdone.authentication.util.UserRegistrationMapper
import com.onecosys.getthingsdone.authorization.TokenRepository
import com.onecosys.getthingsdone.authorization.model.Token
import com.onecosys.getthingsdone.error.AccountVerificationException
import com.onecosys.getthingsdone.error.SignUpException
import com.onecosys.getthingsdone.error.UsernamePasswordMismatchException
import com.onecosys.getthingsdone.user.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class AuthenticationServiceImpl(
    private val tokenRepository: TokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val mapper: UserRegistrationMapper,
    private val userRepository: UserRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val emailService: EmailService
) : AuthenticationService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun signUp(request: RegisterRequest): String {
        checkForSignUpMistakes(request)

        val user = mapper.toEntity(request, passwordEncoder)

        val savedUser = userRepository.save(user)

        val token = UUID.randomUUID().toString()
        val verificationToken = VerificationToken(
            token = token,
            user = savedUser,
            expiryDate = Instant.now().plus(1, ChronoUnit.DAYS)
        )
        verificationTokenRepository.save(verificationToken)

        emailService.sendVerificationEmail(savedUser, token)

        return "Please check your emails to verify your account."
    }


    override fun verifyUser(token: String): String {
        val verificationToken =
            verificationTokenRepository.findByToken(token) ?: throw AccountVerificationException("Invalid Token")

        if (verificationToken.isExpired()) {
            throw AccountVerificationException("Token Expired")
        }

        val user = verificationToken.user
        if (user.isVerified) {
            throw AccountVerificationException("Account Already Verified")
        }

        user.isVerified = true
        userRepository.save(user)
        return "Account Verified Successfully"
    }


    private fun checkForSignUpMistakes(request: RegisterRequest) {
        userRepository.findByEmail(request.email)?.let {
            log.error("Can't find email: $request")
            throw SignUpException("User email already exists!")
        }

        userRepository.findBy_username(request.username)?.let {
            log.error("Can't find username: $request")
            throw SignUpException("Username already exists!")
        }

        if (request.password != request.passwordConfirmation) {
            log.error("Password and password confirmation does not match: $request")
            throw SignUpException("Password and password confirmation does not match!")
        }
    }

    @Transactional
    override fun signIn(request: AuthenticationRequest): AuthenticationResponse {
        val user = userRepository.findBy_username(request.username) ?: throw UsernameNotFoundException("User not found")
        if (!user.isVerified) {
            log.error("user not verified: $user")
            throw SignUpException("You didn't clicked yet on the verification email link")
        }

        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))
        } catch (e: BadCredentialsException) {
            log.error("Username or password is incorrect: $user")
            throw UsernamePasswordMismatchException("Username or password is incorrect")
        }

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
