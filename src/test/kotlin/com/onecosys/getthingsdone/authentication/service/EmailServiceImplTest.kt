package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.error.SignUpException
import com.onecosys.getthingsdone.user.entity.User
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mail.javamail.JavaMailSender


@ExtendWith(MockKExtension::class)
internal class EmailServiceImplTest {

    @RelaxedMockK
    private lateinit var mockMailSender: JavaMailSender

    @RelaxedMockK
    private lateinit var mockMimeMessage: MimeMessage

    private lateinit var objectUnderTest: EmailServiceImpl

    private val dummyToken = "a12b34c56"

    private val user = User(
        email = "test@aon.at",
        _password = "password",
        firstName = "Hamad",
        lastName = "Al Khoury",
        _username = "abu-ali"
    )


    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        objectUnderTest = EmailServiceImpl(mockMailSender)
    }

    @Test
    fun `when send email verification is triggered then expect email successfully send`() {
        every { mockMailSender.createMimeMessage() } returns mockMimeMessage

        objectUnderTest.sendVerificationEmail(user, dummyToken)

        verify(exactly = 1) { mockMailSender.createMimeMessage() }
        verify(exactly = 1) { mockMailSender.send(any<MimeMessage>()) }
    }

    @Test
    fun `when send email verification is triggered then expect messaging exception`() {
        every { mockMailSender.send(any<MimeMessage>()) } throws MessagingException("Simulated error")

        val actualException = assertThrows<SignUpException> { objectUnderTest.sendVerificationEmail(user, dummyToken) }

        assertEquals("failed to send email", actualException.message)
        verify(exactly = 1) { mockMailSender.createMimeMessage() }
        verify(exactly = 1) { mockMailSender.send(any<MimeMessage>()) }
    }

    @Test
    fun `when send password reset email is triggered then expect password successfully reset`() {
        val password = "test-password"
        every { mockMailSender.createMimeMessage() } returns mockMimeMessage

        objectUnderTest.sendPasswordResetEmail(user, password)

        verify(exactly = 1) { mockMailSender.createMimeMessage() }
        verify(exactly = 1) { mockMailSender.send(any<MimeMessage>()) }
    }

    @Test
    fun `when send password reset email is triggered then expect messaging exception`() {
        val password = "test-password"
        every { mockMailSender.send(any<MimeMessage>()) } throws MessagingException("Simulated error")

        val actualException = assertThrows<SignUpException> {
            objectUnderTest.sendPasswordResetEmail(user, password)
        }

        assertEquals("failed to send email", actualException.message)
        verify(exactly = 1) { mockMailSender.createMimeMessage() }
        verify(exactly = 1) { mockMailSender.send(any<MimeMessage>()) }
    }
}