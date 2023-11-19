package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.authentication.dto.VerificationToken
import com.onecosys.getthingsdone.authentication.repository.VerificationTokenRepository
import com.onecosys.getthingsdone.authentication.util.UserRegistrationMapper
import com.onecosys.getthingsdone.error.AccountVerificationException
import com.onecosys.getthingsdone.error.SignUpException
import com.onecosys.getthingsdone.error.TokenExpiredException
import com.onecosys.getthingsdone.user.model.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
internal class AuthenticationServiceImplTest {

    @RelaxedMockK
    private lateinit var mockPasswordEncoder: PasswordEncoder

    @RelaxedMockK
    private lateinit var mockJwtService: JwtService

    @RelaxedMockK
    private lateinit var mockAuthenticationManager: AuthenticationManager

    @RelaxedMockK
    private lateinit var mockMapper: UserRegistrationMapper

    @RelaxedMockK
    private lateinit var mockUserRepository: UserRepository

    @RelaxedMockK
    private lateinit var mockVerificationTokenRepository: VerificationTokenRepository

    @RelaxedMockK
    private lateinit var mockEmailService: EmailService

    private val request = RegisterRequest(
        "John",
        "Doe", "john@example.com",
        "john-doe",
        "password",
        "password"
    )

    private val user = User(
        firstName = request.firstName,
        lastName = request.lastName,
        email = request.email,
        _username = request.username,
        _password = request.password
    )

    private val verificationToken = VerificationToken(
        token = "some-token",
        user = user,
        expiryDate = Instant.now().plus(1, ChronoUnit.DAYS)
    )

    private lateinit var objectUnderTest: AuthenticationService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        objectUnderTest = AuthenticationServiceImpl(
            mockPasswordEncoder,
            mockJwtService,
            mockAuthenticationManager,
            mockMapper,
            mockUserRepository,
            mockVerificationTokenRepository,
            mockEmailService
        )
    }


    @Test
    fun `when user sign up is triggerred then expect success message `() {
        every { mockUserRepository.findBy_username(any()) } returns null
        every { mockUserRepository.findByEmail(any()) } returns null
        every { mockMapper.toEntity(any(), any()) } returns user
        every { mockUserRepository.save(any()) } returns user
        every { mockVerificationTokenRepository.save(any()) } returns verificationToken

        val actualResult = objectUnderTest.signUp(request)

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

        val actualResult = assertThrows<SignUpException> { objectUnderTest.signUp(request) }

        assertEquals("User email already exists!", actualResult.message)
        verify { mockUserRepository.save(user) wasNot called }
        verify { mockVerificationTokenRepository.save(any()) wasNot called }
    }

    @Test
    fun `when user sign up is triggerred then expect sign up exception for username `() {
        every { mockUserRepository.findByEmail(any()) } returns null
        every { mockUserRepository.findBy_username(any()) } returns user

        val actualResult = assertThrows<SignUpException> { objectUnderTest.signUp(request) }

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
        every { mockEmailService.sendVerificationEmail(user, verificationToken.token) } returns Unit

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
}