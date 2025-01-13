package com.onecosys.getthingsdone.auth.service

import com.onecosys.getthingsdone.auth.domain.AppUser
import com.onecosys.getthingsdone.auth.domain.SignUpException
import com.onecosys.getthingsdone.auth.infrastructure.EmailServiceImpl
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

    companion object {
        private const val DUMMY_TOKEN = "a12b34c56"
        private const val DUMMY_PASSWORD = "dummy-pw"
    }

    private val mockMailSender = mockk<JavaMailSender>(relaxed = true)

    private val mockMimeMessage = mockk<MimeMessage>(relaxed = true)

    private val objectUnderTest = EmailServiceImpl(mockMailSender)

    private val appUser = AppUser(
        email = "test@aon.at",
        appPassword = DUMMY_PASSWORD,
        firstName = "Hamad",
        lastName = "Al Khoury",
        appUsername = "abu-ali"
    )


    @Test
    fun `when send email verification is triggered then expect email successfully send`() {
        every { mockMailSender.createMimeMessage() } returns mockMimeMessage
        every { mockMailSender.send(any<MimeMessage>()) } returns Unit

        objectUnderTest.sendVerificationEmail(appUser, DUMMY_TOKEN)

        verify(exactly = 1) { mockMailSender.createMimeMessage() }
        verify(exactly = 1) { mockMailSender.send(any<MimeMessage>()) }
    }

    @Test
    fun `when send email verification is triggered then expect messaging exception`() {
        every { mockMailSender.createMimeMessage() } throws SignUpException("failed to send email")

        val actualException =
            assertThrows<SignUpException> { objectUnderTest.sendVerificationEmail(appUser, DUMMY_TOKEN) }

        assertEquals("failed to send email", actualException.message)
        verify(exactly = 1) { mockMailSender.createMimeMessage() }
        verify(exactly = 0) { mockMailSender.send(any<MimeMessage>()) }
    }

    @Test
    fun `when send password reset email is triggered then expect password successfully reset`() {
        every { mockMailSender.createMimeMessage() } returns mockMimeMessage

        objectUnderTest.sendPasswordResetEmail(appUser, DUMMY_PASSWORD)

        verify(exactly = 1) { mockMailSender.createMimeMessage() }
        verify(exactly = 1) { mockMailSender.send(any<MimeMessage>()) }
    }

    @Test
    fun `when send password reset email is triggered then expect messaging exception`() {
        every { mockMailSender.send(any<MimeMessage>()) } throws MessagingException("Simulated error")

        val actualException = assertThrows<SignUpException> {
            objectUnderTest.sendPasswordResetEmail(appUser, DUMMY_PASSWORD)
        }

        assertEquals("failed to send email", actualException.message)
        verify(exactly = 1) { mockMailSender.createMimeMessage() }
        verify(exactly = 1) { mockMailSender.send(any<MimeMessage>()) }
    }
}