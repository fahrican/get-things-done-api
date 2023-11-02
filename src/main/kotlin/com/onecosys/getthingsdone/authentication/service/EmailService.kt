package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.user.entity.User
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(private val mailSender: JavaMailSender) {
    fun sendVerificationEmail(user: User, token: String) {
        val message = SimpleMailMessage()
        message.setTo(user.email)
        message.subject = "Email Verification"
        message.text = ("To verify your account, please click here : "
                + "http://localhost:8080/api/v1/auth/verify?token=$token")
        mailSender.send(message)
    }
}
