package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.dto.AuthenticationRequest
import com.onecosys.getthingsdone.authentication.dto.AuthenticationResponse
import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.authentication.dto.VerificationResponse

interface AuthenticationService {

    fun signUp(request: RegisterRequest): VerificationResponse

    fun verifyUser(token: String): VerificationResponse

    fun signIn(request: AuthenticationRequest): AuthenticationResponse
}