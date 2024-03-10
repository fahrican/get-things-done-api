package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.user.entity.User

interface EmailService {

    fun sendVerificationEmail(user: User, token: String)

    fun sendPasswordResetEmail(user: User, newPassword: String)
}