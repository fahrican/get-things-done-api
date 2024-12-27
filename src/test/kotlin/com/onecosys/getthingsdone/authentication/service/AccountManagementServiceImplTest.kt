package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.entity.VerificationToken
import com.onecosys.getthingsdone.authentication.repository.VerificationTokenRepository
import com.onecosys.getthingsdone.authentication.util.SignUpMapper
import com.onecosys.getthingsdone.dto.AuthenticationRequest
import com.onecosys.getthingsdone.dto.RegisterRequest
import com.onecosys.getthingsdone.error.AccountVerificationException
import com.onecosys.getthingsdone.error.SignUpException
import com.onecosys.getthingsdone.error.TokenExpiredException
import com.onecosys.getthingsdone.error.UserNotFoundException
import com.onecosys.getthingsdone.error.UsernamePasswordMismatchException
import com.onecosys.getthingsdone.user.entity.AppUser
import com.onecosys.getthingsdone.user.repository.AppUserRepository
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

internal class AccountManagementServiceImplTest {

    companion object {
        private const val DUMMY_TOKEN = "some-token"
        private const val DUMMY_PASSWORD = "dummy-pw"

    }

    private val mockPasswordEncoder = mockk<PasswordEncoder>()

    private val mockJwtService = mockk<JwtService>()

    private val mockAuthenticationManager = mockk<AuthenticationManager>()

    private val mockMapper = mockk<SignUpMapper>()

    private val mockAppUserRepository = mockk<AppUserRepository>()

    private val mockVerificationTokenRepository = mockk<VerificationTokenRepository>()

    private val mockEmailService = mockk<EmailService>()

    private val registerRequest = RegisterRequest(
        "John",
        "Doe", "john@example.com",
        "john-doe",
        DUMMY_PASSWORD,
        DUMMY_PASSWORD
    )

    private val appUser = AppUser(
        firstName = registerRequest.firstName,
        lastName = registerRequest.lastName,
        email = registerRequest.email,
        appUsername = registerRequest.username,
        appPassword = registerRequest.password
    )

    private val verificationToken = VerificationToken(
        token = DUMMY_TOKEN,
        appUser = appUser,
        expiryDate = Instant.now().plus(1, ChronoUnit.DAYS)
    )

    private val authenticationRequest = AuthenticationRequest("abu-ali", DUMMY_PASSWORD)

    private val objectUnderTest = AccountManagementServiceImpl(
        mockPasswordEncoder,
        mockJwtService,
        mockAuthenticationManager,
        mockMapper,
        mockAppUserRepository,
        mockVerificationTokenRepository,
        mockEmailService
    )


    @Test
    fun `when user sign up is triggerred then expect success message `() {
        every { mockAppUserRepository.findByAppUsername(any()) } returns null
        every { mockAppUserRepository.findByEmail(any()) } returns null
        every { mockMapper.toEntity(any(), any()) } returns appUser
        every { mockAppUserRepository.save(any()) } returns appUser
        every { mockVerificationTokenRepository.save(any()) } returns verificationToken
        every { mockEmailService.sendVerificationEmail(any(), any()) } returns Unit

        val actualResult = objectUnderTest.signUp(registerRequest)

        assertEquals(
            "Please, check your emails and spam/junk folder for ${appUser.email} to verify your account",
            actualResult.message
        )
        verify(exactly = 1) { mockAppUserRepository.save(appUser) }
        verify(exactly = 1) { mockVerificationTokenRepository.save(any()) }
    }

    @Test
    fun `when user sign up is triggerred then expect sign up exception for email `() {
        every { mockAppUserRepository.findByEmail(any()) } returns appUser

        val actualResult = assertThrows<SignUpException> { objectUnderTest.signUp(registerRequest) }

        assertEquals("User email already exists!", actualResult.message)
        verify { mockAppUserRepository.save(appUser) wasNot called }
        verify { mockVerificationTokenRepository.save(any()) wasNot called }
    }

    @Test
    fun `when user sign up is triggerred then expect sign up exception for username `() {
        every { mockAppUserRepository.findByEmail(any()) } returns null
        every { mockAppUserRepository.findByAppUsername(any()) } returns appUser

        val actualResult = assertThrows<SignUpException> { objectUnderTest.signUp(registerRequest) }

        assertEquals("Username already exists!", actualResult.message)
        verify { mockAppUserRepository.save(appUser) wasNot called }
        verify { mockVerificationTokenRepository.save(any()) wasNot called }
    }

    @Test
    fun `when user sign up is triggerred then expect sign up exception for password confirmation `() {
        every { mockAppUserRepository.findByEmail(any()) } returns null
        every { mockAppUserRepository.findByAppUsername(any()) } returns null
        val request = RegisterRequest(
            "John",
            "Doe", "john@example.com",
            "john-doe",
            "password123",
            "password456"
        )

        val actualResult = assertThrows<SignUpException> { objectUnderTest.signUp(request) }

        assertEquals("Password and password confirmation does not match!", actualResult.message)
        verify { mockAppUserRepository.save(appUser) wasNot called }
        verify { mockVerificationTokenRepository.save(any()) wasNot called }
    }

    @Test
    fun `when verify user is triggerred then expect account verified successfully response`() {
        every { mockVerificationTokenRepository.findByToken(DUMMY_TOKEN) } returns verificationToken
        every { mockAppUserRepository.save(appUser) } returns appUser

        val result = objectUnderTest.verifyUser(DUMMY_TOKEN)

        verify(exactly = 1) { mockAppUserRepository.save(appUser) }
        assertEquals("Account Verified Successfully", result.message)
        assertTrue(appUser.isVerified)
    }

    @Test
    fun `when verify user is triggerred then expect token is expired`() {
        verificationToken.expiryDate = Instant.now().minus(1, ChronoUnit.DAYS)
        every { mockVerificationTokenRepository.findByToken(DUMMY_TOKEN) } returns verificationToken
        every { mockVerificationTokenRepository.save(verificationToken) } returns verificationToken
        every { mockEmailService.sendVerificationEmail(any(), any()) } returns Unit

        val actualResult = assertThrows<TokenExpiredException> { objectUnderTest.verifyUser(DUMMY_TOKEN) }

        assertEquals(
            "Token expired. A new verification link has been sent to your email: ${appUser.email}",
            actualResult.message
        )
        verify { mockEmailService.sendVerificationEmail(appUser, any()) }
    }

    @Test
    fun `when verify user is triggerred then expect account verification exception`() {
        every { mockVerificationTokenRepository.findByToken(DUMMY_TOKEN) } returns verificationToken
        appUser.isVerified = true

        val actualResult = assertThrows<AccountVerificationException> { objectUnderTest.verifyUser(DUMMY_TOKEN) }

        assertEquals(
            "Account Already Verified",
            actualResult.message
        )
        verify(exactly = 1) { mockAppUserRepository.save(appUser) wasNot called }
    }

    @Test
    fun `when sign in user is triggerred then expect authentication response`() {
        val refreshToken = "refresh-token"
        val mockAuthentication: Authentication = mockk(relaxed = true)
        every { mockAppUserRepository.findByAppUsername(any()) } returns appUser
        appUser.isVerified = true
        every { mockAuthenticationManager.authenticate(any()) } returns mockAuthentication
        every { mockJwtService.generateAccessToken(appUser) } returns DUMMY_TOKEN
        every { mockJwtService.generateRefreshToken(appUser) } returns refreshToken

        val result = objectUnderTest.signIn(authenticationRequest)

        assertEquals(DUMMY_TOKEN, result.accessToken)
        assertEquals(refreshToken, result.refreshToken)
    }

    @Test
    fun `when sign in user is triggerred then expect sign up exception`() {
        appUser.isVerified = false
        every { mockAppUserRepository.findByAppUsername(any()) } returns appUser

        val actualResult = assertThrows<SignUpException> { objectUnderTest.signIn(authenticationRequest) }

        assertEquals(
            "You didn't clicked yet on the verification link, check your email: ${appUser.email}",
            actualResult.message
        )
        verify { mockAuthenticationManager.authenticate(any()) wasNot called }
    }

    @Test
    fun `when sign in user is triggerred then expect username password mismatch exception`() {
        appUser.isVerified = true
        val bce = BadCredentialsException("Username or password is incorrect")
        every { mockAppUserRepository.findByAppUsername(any()) } returns appUser
        every { mockAuthenticationManager.authenticate(any()) } throws bce


        val actualResult =
            assertThrows<UsernamePasswordMismatchException> { objectUnderTest.signIn(authenticationRequest) }

        assertEquals(bce.message, actualResult.message)
    }

    @Test
    fun `when request password reset is triggered then expect email confirmed response`() {
        val password = "test123"
        every { mockAppUserRepository.findByEmail(appUser.email) } returns appUser
        every { mockPasswordEncoder.encode(any()) } returns password
        every { mockAppUserRepository.save(appUser) } returns appUser
        every { mockEmailService.sendPasswordResetEmail(any(), any()) } returns Unit

        val actualResult = objectUnderTest.requestPasswordReset(appUser.email)

        assertEquals("New password sent to ${appUser.email}", actualResult.message)
        verify(exactly = 1) { mockAppUserRepository.findByEmail(appUser.email) }
        verify(exactly = 1) { mockPasswordEncoder.encode(any()) }
        verify(exactly = 1) { mockAppUserRepository.save(appUser) }
        verify(exactly = 1) { mockEmailService.sendPasswordResetEmail(any(), any()) }
    }

    @Test
    fun `when request password reset is triggered then expect user not found exception`() {
        val userNotFoundException = UserNotFoundException("E-Mail: ${appUser.email} does not exist!")
        every { mockAppUserRepository.findByEmail(appUser.email) } throws userNotFoundException

        val actualResult = assertThrows<UserNotFoundException> { objectUnderTest.requestPasswordReset(appUser.email) }

        assertEquals("E-Mail: ${appUser.email} does not exist!", actualResult.message)
        verify(exactly = 1) { mockAppUserRepository.findByEmail(appUser.email) }
        verify(exactly = 0) { mockPasswordEncoder.encode(any()) }
        verify(exactly = 0) { mockAppUserRepository.save(appUser) }
        verify(exactly = 0) { mockEmailService.sendPasswordResetEmail(any(), any()) }
    }
}