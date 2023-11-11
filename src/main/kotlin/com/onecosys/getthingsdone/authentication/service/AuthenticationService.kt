package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.dto.AuthenticationRequest
import com.onecosys.getthingsdone.authentication.dto.AuthenticationResponse
import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.authentication.dto.EmailConfirmedResponse

interface AuthenticationService {

    fun signUp(request: RegisterRequest): EmailConfirmedResponse

    fun verifyUser(token: String): EmailConfirmedResponse

    fun signIn(request: AuthenticationRequest): AuthenticationResponse

    fun requestPasswordReset(email: String): EmailConfirmedResponse
}