package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.dto.AuthenticationRequest
import com.onecosys.getthingsdone.authentication.dto.AuthenticationResponse
import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.authentication.dto.EmailConfirmedResponse
import com.onecosys.getthingsdone.authentication.dto.VerificationToken
import com.onecosys.getthingsdone.authentication.repository.VerificationTokenRepository
import com.onecosys.getthingsdone.authentication.util.UserRegistrationMapper
import com.onecosys.getthingsdone.error.AccountVerificationException
import com.onecosys.getthingsdone.error.SignUpException
import com.onecosys.getthingsdone.error.TokenExpiredException
import com.onecosys.getthingsdone.error.UserNotFoundException
import com.onecosys.getthingsdone.error.UsernamePasswordMismatchException
import com.onecosys.getthingsdone.user.model.entity.User
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
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val mapper: UserRegistrationMapper,
    private val userRepository: UserRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val emailService: EmailService
) : AuthenticationService {

    companion object  {
        private const val TEN_CHARACTERS = 10
    }

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun signUp(request: RegisterRequest): EmailConfirmedResponse {
        checkForSignUpMistakes(request)

        val user = mapper.toEntity(request, passwordEncoder)
        val savedUser = userRepository.save(user)
        val (token, verificationToken) = initiateEmailVerificationToken(savedUser)

        verificationTokenRepository.save(verificationToken)
        emailService.sendVerificationEmail(savedUser, token)

        return EmailConfirmedResponse("Please, check your emails and spam/junk folder for ${user.email} to verify your account")
    }

    override fun verifyUser(token: String): EmailConfirmedResponse {
        val currentVerificationToken: VerificationToken =
            verificationTokenRepository.findByToken(token) ?: throw AccountVerificationException("Invalid Token")

        val user = currentVerificationToken.user
        if (currentVerificationToken.isExpired()) {
            log.error("Token Expired for user: $user")
            currentVerificationToken.token = UUID.randomUUID().toString()
            currentVerificationToken.expiryDate = Instant.now().plus(15, ChronoUnit.MINUTES)
            verificationTokenRepository.save(currentVerificationToken)
            emailService.sendVerificationEmail(user, currentVerificationToken.token)
            throw TokenExpiredException("Token expired. A new verification link has been sent to your email: ${user.email}")
        }

        if (user.isVerified) {
            log.error("Account Already Verified: $user")
            throw AccountVerificationException("Account Already Verified")
        }

        user.isVerified = true
        userRepository.save(user)
        return EmailConfirmedResponse("Account Verified Successfully")
    }

    @Transactional
    override fun signIn(request: AuthenticationRequest): AuthenticationResponse {
        val user = userRepository.findBy_username(request.username) ?: throw UsernameNotFoundException("User not found")
        if (!user.isVerified) {
            log.error("user not verified: $user")
            throw SignUpException("You didn't clicked yet on the verification link, check your email: ${user.email}")
        }

        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))
        } catch (bce: BadCredentialsException) {
            log.error("Username or password is incorrect: ${bce.message}")
            throw UsernamePasswordMismatchException("Username or password is incorrect")
        }

        val jwtToken = jwtService.generateAccessToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)

        return AuthenticationResponse(jwtToken, refreshToken)
    }

    override fun requestPasswordReset(email: String): EmailConfirmedResponse {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException("E-Mail: $email does not exist!")

        val newPassword = UUID.randomUUID().toString().take(TEN_CHARACTERS)
        user._password = passwordEncoder.encode(newPassword)
        userRepository.save(user)

        emailService.sendPasswordResetEmail(user, newPassword)
        return EmailConfirmedResponse("New password sent to $email")
    }

    private fun initiateEmailVerificationToken(user: User): Pair<String, VerificationToken> {
        val token = UUID.randomUUID().toString()
        val verificationToken = VerificationToken(
            token = token,
            user = user,
            expiryDate = Instant.now().plus(15, ChronoUnit.MINUTES)
        )
        return Pair(token, verificationToken)
    }

    private fun checkForSignUpMistakes(request: RegisterRequest) {
        userRepository.findByEmail(request.email)?.let {
            log.error("User email already exists: $request")
            throw SignUpException("User email already exists!")
        }

        userRepository.findBy_username(request.username)?.let {
            log.error("Username already exists: $request")
            throw SignUpException("Username already exists!")
        }

        if (request.password != request.passwordConfirmation) {
            log.error("Password and password confirmation does not match: $request")
            throw SignUpException("Password and password confirmation does not match!")
        }
    }
}
