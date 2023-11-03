package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.dto.AuthenticationRequest
import com.onecosys.getthingsdone.authentication.dto.AuthenticationResponse
import com.onecosys.getthingsdone.authentication.dto.RegisterRequest

interface AuthenticationService {

    fun signUp(request: RegisterRequest): String

    fun verifyUser(token: String): String

    fun signIn(request: AuthenticationRequest): AuthenticationResponse
}