package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.entity.VerificationToken
import com.onecosys.getthingsdone.authentication.repository.VerificationTokenRepository
import com.onecosys.getthingsdone.authentication.util.UserRegistrationMapper
import com.onecosys.getthingsdone.error.AccountVerificationException
import com.onecosys.getthingsdone.error.SignUpException
import com.onecosys.getthingsdone.error.TokenExpiredException
import com.onecosys.getthingsdone.error.UserNotFoundException
import com.onecosys.getthingsdone.error.UsernamePasswordMismatchException
import com.onecosys.getthingsdone.models.AuthenticationRequest
import com.onecosys.getthingsdone.models.RegisterRequest
import com.onecosys.getthingsdone.user.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import java.time.temporal.ChronoUnit

internal class AuthUserServiceImplTest {

    private val mockPasswordEncoder = mockk<PasswordEncoder>()

    private val mockJwtService = mockk<JwtService>()

    private val mockAuthenticationManager = mockk<AuthenticationManager>()

    private val mockMapper = mockk<UserRegistrationMapper>()

    private val mockUserRepository = mockk<UserRepository>()

    private val mockVerificationTokenRepository = mockk<VerificationTokenRepository>()

    private val mockEmailService = mockk<EmailService>()

    private val registerRequest = RegisterRequest(
        "John",
        "Doe", "john@example.com",
        "john-doe",
        "password",
        "password"
    )

    private val user = User(
        firstName = registerRequest.firstName,
        lastName = registerRequest.lastName,
        email = registerRequest.email,
        _username = registerRequest.username,
        _password = registerRequest.password
    )

    private val verificationToken = VerificationToken(
        token = "some-token",
        user = user,
        expiryDate = Instant.now().plus(1, ChronoUnit.DAYS)
    )

    private val authenticationRequest = AuthenticationRequest("abu-ali", "password")

    private val objectUnderTest = AccountManagementServiceImpl(
        mockPasswordEncoder,
        mockJwtService,
        mockAuthenticationManager,
        mockMapper,
        mockUserRepository,
        mockVerificationTokenRepository,
        mockEmailService
    )


    @Test
    fun `when user sign up is triggerred then expect success message `() {
        every { mockUserRepository.findBy_username(any()) } returns null
        every { mockUserRepository.findByEmail(any()) } returns null
        every { mockMapper.toEntity(any(), any()) } returns user
        every { mockUserRepository.save(any()) } returns user
        every { mockVerificationTokenRepository.save(any()) } returns verificationToken
        every { mockEmailService.sendVerificationEmail(any(), any()) } returns Unit

        val actualResult = objectUnderTest.signUp(registerRequest)

        assertEquals(
            "Please, check your emails and spam/junk folder for ${user.email} to verify your account",
            actualResult.message
        )
        verify(exactly = 1) { mockUserRepository.save(user) }
        verify(exactly = 1) { mockVerificationTokenRepository.save(any()) }
    }

    @Test
    fun `when user sign up is triggerred then expect sign up exception for email `() {
        every { mockUserRepository.findByEmail(any()) } returns user

        val actualResult = assertThrows<SignUpException> { objectUnderTest.signUp(registerRequest) }

        assertEquals("User email already exists!", actualResult.message)
        verify { mockUserRepository.save(user) wasNot called }
        verify { mockVerificationTokenRepository.save(any()) wasNot called }
    }

    @Test
    fun `when user sign up is triggerred then expect sign up exception for username `() {
        every { mockUserRepository.findByEmail(any()) } returns null
        every { mockUserRepository.findBy_username(any()) } returns user

        val actualResult = assertThrows<SignUpException> { objectUnderTest.signUp(registerRequest) }

        assertEquals("Username already exists!", actualResult.message)
        verify { mockUserRepository.save(user) wasNot called }
        verify { mockVerificationTokenRepository.save(any()) wasNot called }
    }

    @Test
    fun `when user sign up is triggerred then expect sign up exception for password confirmation `() {
        every { mockUserRepository.findByEmail(any()) } returns null
        every { mockUserRepository.findBy_username(any()) } returns null
        val request = RegisterRequest(
            "John",
            "Doe", "john@example.com",
            "john-doe",
            "password123",
            "password456"
        )

        val actualResult = assertThrows<SignUpException> { objectUnderTest.signUp(request) }

        assertEquals("Password and password confirmation does not match!", actualResult.message)
        verify { mockUserRepository.save(user) wasNot called }
        verify { mockVerificationTokenRepository.save(any()) wasNot called }
    }

    @Test
    fun `when verify user is triggerred then expect account verified successfully response`() {
        val token = "some-token"
        every { mockVerificationTokenRepository.findByToken(token) } returns verificationToken
        every { mockUserRepository.save(user) } returns user

        val result = objectUnderTest.verifyUser(token)

        verify(exactly = 1) { mockUserRepository.save(user) }
        assertEquals("Account Verified Successfully", result.message)
        assertTrue(user.isVerified)
    }

    @Test
    fun `when verify user is triggerred then expect token is expired`() {
        val token = "expired-token"
        verificationToken.expiryDate = Instant.now().minus(1, ChronoUnit.DAYS)
        every { mockVerificationTokenRepository.findByToken(token) } returns verificationToken
        every { mockVerificationTokenRepository.save(verificationToken) } returns verificationToken
        every { mockEmailService.sendVerificationEmail(any(), any()) } returns Unit

        val actualResult = assertThrows<TokenExpiredException> { objectUnderTest.verifyUser(token) }

        assertEquals(
            "Token expired. A new verification link has been sent to your email: ${user.email}",
            actualResult.message
        )
        verify { mockEmailService.sendVerificationEmail(user, any()) }
    }

    @Test
    fun `when verify user is triggerred then expect account verification exception`() {
        val token = "some-token"
        every { mockVerificationTokenRepository.findByToken(token) } returns verificationToken
        user.isVerified = true

        val actualResult = assertThrows<AccountVerificationException> { objectUnderTest.verifyUser(token) }

        assertEquals(
            "Account Already Verified",
            actualResult.message
        )
        verify(exactly = 1) { mockUserRepository.save(user) wasNot called }
    }

    @Test
    fun `when sign in user is triggerred then expect authentication response`() {
        val jwtToken = "jwt-token"
        val refreshToken = "refresh-token"
        val mockAuthentication: Authentication = mockk(relaxed = true)
        every { mockUserRepository.findBy_username(any()) } returns user
        user.isVerified = true
        every { mockAuthenticationManager.authenticate(any()) } returns mockAuthentication
        every { mockJwtService.generateAccessToken(user) } returns jwtToken
        every { mockJwtService.generateRefreshToken(user) } returns refreshToken

        val result = objectUnderTest.signIn(authenticationRequest)

        assertEquals(jwtToken, result.accessToken)
        assertEquals(refreshToken, result.refreshToken)
    }

    @Test
    fun `when sign in user is triggerred then expect sign up exception`() {
        user.isVerified = false
        every { mockUserRepository.findBy_username(any()) } returns user

        val actualResult = assertThrows<SignUpException> { objectUnderTest.signIn(authenticationRequest) }

        assertEquals(
            "You didn't clicked yet on the verification link, check your email: ${user.email}",
            actualResult.message
        )
        verify { mockAuthenticationManager.authenticate(any()) wasNot called }
    }

    @Test
    fun `when sign in user is triggerred then expect username password mismatch exception`() {
        user.isVerified = true
        val bce = BadCredentialsException("Username or password is incorrect")
        every { mockUserRepository.findBy_username(any()) } returns user
        every { mockAuthenticationManager.authenticate(any()) } throws bce


        val actualResult =
            assertThrows<UsernamePasswordMismatchException> { objectUnderTest.signIn(authenticationRequest) }

        assertEquals(bce.message, actualResult.message)
    }

    @Test
    fun `when request password reset is triggered then expect email confirmed response`() {
        val password = "test123"
        every { mockUserRepository.findByEmail(user.email) } returns user
        every { mockPasswordEncoder.encode(any()) } returns password
        every { mockUserRepository.save(user) } returns user
        every { mockEmailService.sendPasswordResetEmail(any(), any()) } returns Unit

        val actualResult = objectUnderTest.requestPasswordReset(user.email)

        assertEquals("New password sent to ${user.email}", actualResult.message)
        verify(exactly = 1) { mockUserRepository.findByEmail(user.email) }
        verify(exactly = 1) { mockPasswordEncoder.encode(any()) }
        verify(exactly = 1) { mockUserRepository.save(user) }
        verify(exactly = 1) { mockEmailService.sendPasswordResetEmail(any(), any()) }
    }

    @Test
    fun `when request password reset is triggered then expect user not found exception`() {
        val userNotFoundException = UserNotFoundException("E-Mail: ${user.email} does not exist!")
        every { mockUserRepository.findByEmail(user.email) } throws userNotFoundException

        val actualResult = assertThrows<UserNotFoundException> { objectUnderTest.requestPasswordReset(user.email) }

        assertEquals("E-Mail: ${user.email} does not exist!", actualResult.message)
        verify(exactly = 1) { mockUserRepository.findByEmail(user.email) }
        verify(exactly = 0) { mockPasswordEncoder.encode(any()) }
        verify(exactly = 0) { mockUserRepository.save(user) }
        verify(exactly = 0) { mockEmailService.sendPasswordResetEmail(any(), any()) }
    }
}