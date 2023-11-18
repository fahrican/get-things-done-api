package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.authentication.dto.VerificationToken
import com.onecosys.getthingsdone.authentication.repository.VerificationTokenRepository
import com.onecosys.getthingsdone.authentication.util.UserRegistrationMapper
import com.onecosys.getthingsdone.task.model.Priority
import com.onecosys.getthingsdone.task.model.dto.TaskCreateRequest
import com.onecosys.getthingsdone.task.model.entity.Task
import com.onecosys.getthingsdone.task.service.TaskServiceImpl
import com.onecosys.getthingsdone.user.model.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
internal class AuthenticationServiceImplTest {

    @RelaxedMockK
    private lateinit var passwordEncoder: PasswordEncoder

    @RelaxedMockK
    private lateinit var jwtService: JwtService

    @RelaxedMockK
    private lateinit var authenticationManager: AuthenticationManager

    @RelaxedMockK
    private lateinit var mapper: UserRegistrationMapper

    @RelaxedMockK
    private lateinit var userRepository: UserRepository

    @RelaxedMockK
    private lateinit var verificationTokenRepository: VerificationTokenRepository

    @RelaxedMockK
    private lateinit var emailService: EmailService

    private lateinit var objectUnderTest: AuthenticationService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        objectUnderTest = AuthenticationServiceImpl(
            passwordEncoder,
            jwtService,
            authenticationManager,
            mapper,
            userRepository,
            verificationTokenRepository,
            emailService
        )
    }


    /*    @Test
        fun signUp_shouldSaveUser_InitiateVerificationToken_AndSendEmail() {
            val request = RegisterRequest("testuser", "test@email.com", "password")
            val savedUser = mockk()
            val verificationToken = mock(VerificationToken::class.java)

            `when`(mockMapper.toEntity(request, mockPasswordEncoder)).thenReturn(savedUser)
            `when`(mockUserRepository.save(savedUser)).thenReturn(savedUser)
            `when`(authenticationService.initiateEmailVerificationToken(savedUser)).thenReturn(
                Pair(
                    "test-token",
                    verificationToken
                )
            )

            val response = authenticationService.signUp(request)

            assertEquals(
                "Please, check your emails and spam/junk folder for test@email.com to verify your account",
                response.message
            )
            verify(mockUserRepository).save(savedUser)
            verify(mockVerificationTokenRepository).save(verificationToken)
            verify(mockEmailService).sendVerificationEmail(savedUser, "test-token")
        }*/


    @Test
    fun `signUp should save user, create verification token, send email, and return confirmation response`() {
        // Arrange
        val request = RegisterRequest("John", "Doe", "john@example.com", "john-doe", "password", "password")
        val user = User(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            _username = request.username,
            _password = request.password
        )
        val token = "some-token"
        val verificationToken = VerificationToken(
            token = token,
            user = user,
            expiryDate = Instant.now().plus(1, ChronoUnit.DAYS)
        )

        every { userRepository.findBy_username(any()) } returns null
        every { userRepository.findByEmail(any()) } returns null
        every { mapper.toEntity(any(), any()) } returns user
        every { userRepository.save(any()) } returns user
        every { verificationTokenRepository.save(any()) } returns verificationToken
        every { emailService.sendVerificationEmail(user, token) } returns Unit

        // Act
        val result = objectUnderTest.signUp(request)

        // Assert
        assertEquals(
            "Please, check your emails and spam/junk folder for ${user.email} to verify your account",
            result.message
        )
        verify { userRepository.save(user) }
        verify { verificationTokenRepository.save(any()) }
    }
}