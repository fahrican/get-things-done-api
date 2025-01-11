package com.onecosys.getthingsdone.authentication.infrastructure

import com.onecosys.getthingsdone.user.entity.AppUser

interface EmailService {

    fun sendVerificationEmail(appUser: AppUser, token: String)

    fun sendPasswordResetEmail(appUser: AppUser, newPassword: String)
}