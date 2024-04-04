package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.error.SignUpException
import com.onecosys.getthingsdone.user.entity.AppUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mail.javamail.JavaMailSender


internal class EmailServiceImplTest {

    private val mockMailSender = mockk<JavaMailSender>(relaxed = true)

    private val mockMimeMessage = mockk<MimeMessage>(relaxed = true)

    private val objectUnderTest = EmailServiceImpl(mockMailSender)

    private val dummyToken = "a12b34c56"

    private val appUser = AppUser(
        email = "test@aon.at",
        appPassword = "password",
        firstName = "Hamad",
        lastName = "Al Khoury",
        appUsername = "abu-ali"
    )


    @Test
    fun `when send email verification is triggered then expect email successfully send`() {
        every { mockMailSender.createMimeMessage() } returns mockMimeMessage
        every { mockMailSender.send(any<MimeMessage>()) } returns Unit

        objectUnderTest.sendVerificationEmail(appUser, dummyToken)

        verify(exactly = 1) { mockMailSender.createMimeMessage() }
        verify(exactly = 1) { mockMailSender.send(any<MimeMessage>()) }
    }

    @Test
    fun `when send email verification is triggered then expect messaging exception`() {
        every { mockMailSender.createMimeMessage() } throws SignUpException("failed to send email")

        val actualException =
            assertThrows<SignUpException> { objectUnderTest.sendVerificationEmail(appUser, dummyToken) }

        assertEquals("failed to send email", actualException.message)
        verify(exactly = 1) { mockMailSender.createMimeMessage() }
        verify(exactly = 0) { mockMailSender.send(any<MimeMessage>()) }
    }

    @Test
    fun `when send password reset email is triggered then expect password successfully reset`() {
        val password = "test-password"
        every { mockMailSender.createMimeMessage() } returns mockMimeMessage

        objectUnderTest.sendPasswordResetEmail(appUser, password)

        verify(exactly = 1) { mockMailSender.createMimeMessage() }
        verify(exactly = 1) { mockMailSender.send(any<MimeMessage>()) }
    }

    @Test
    fun `when send password reset email is triggered then expect messaging exception`() {
        val password = "test-password"
        every { mockMailSender.send(any<MimeMessage>()) } throws MessagingException("Simulated error")

        val actualException = assertThrows<SignUpException> {
            objectUnderTest.sendPasswordResetEmail(appUser, password)
        }

        assertEquals("failed to send email", actualException.message)
        verify(exactly = 1) { mockMailSender.createMimeMessage() }
        verify(exactly = 1) { mockMailSender.send(any<MimeMessage>()) }
    }
}