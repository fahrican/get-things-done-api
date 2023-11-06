package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.user.model.entity.User

interface EmailService {

    fun sendVerificationEmail(user: User, token: String)
}