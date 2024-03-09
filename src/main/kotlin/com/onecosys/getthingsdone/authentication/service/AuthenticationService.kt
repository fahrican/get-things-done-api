package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.models.AuthenticationRequest
import com.onecosys.getthingsdone.models.AuthenticationResponse
import com.onecosys.getthingsdone.models.EmailConfirmedResponse
import com.onecosys.getthingsdone.models.RegisterRequest


interface AuthenticationService {

    fun signUp(request: RegisterRequest): EmailConfirmedResponse

    fun verifyUser(token: String): EmailConfirmedResponse

    fun signIn(request: AuthenticationRequest): AuthenticationResponse

    fun requestPasswordReset(email: String): EmailConfirmedResponse
}