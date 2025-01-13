package com.onecosys.getthingsdone.auth.application

import com.onecosys.getthingsdone.auth.domain.AppUser

interface EmailService {

    fun sendVerificationEmail(appUser: AppUser, token: String)

    fun sendPasswordResetEmail(appUser: AppUser, newPassword: String)
}