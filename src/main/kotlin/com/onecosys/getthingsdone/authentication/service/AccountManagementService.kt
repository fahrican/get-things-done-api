package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.dto.AuthenticationRequest
import com.onecosys.getthingsdone.dto.AuthenticationResponse
import com.onecosys.getthingsdone.dto.EmailConfirmedResponse
import com.onecosys.getthingsdone.dto.RegisterRequest


interface AccountManagementService {

    fun signUp(request: RegisterRequest): EmailConfirmedResponse

    fun verifyUser(token: String): EmailConfirmedResponse

    fun signIn(request: AuthenticationRequest): AuthenticationResponse

    fun requestPasswordReset(email: String): EmailConfirmedResponse
}