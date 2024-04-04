package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.user.entity.AppUser

interface EmailService {

    fun sendVerificationEmail(appUser: AppUser, token: String)

    fun sendPasswordResetEmail(appUser: AppUser, newPassword: String)
}