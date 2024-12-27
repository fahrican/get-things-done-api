package com.onecosys.getthingsdone.authentication.web.rest

import com.onecosys.getthingsdone.api.AuthenticationResource
import com.onecosys.getthingsdone.authentication.service.AccountManagementService
import com.onecosys.getthingsdone.dto.AuthenticationRequest
import com.onecosys.getthingsdone.dto.AuthenticationResponse
import com.onecosys.getthingsdone.dto.EmailConfirmedResponse
import com.onecosys.getthingsdone.dto.RegisterRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationController(
    private val service: AccountManagementService
) : AuthenticationResource {

    override fun requestPasswordReset(
        email: String
    ): ResponseEntity<EmailConfirmedResponse> = ResponseEntity.ok(service.requestPasswordReset(email))

    override fun signIn(
        authenticationRequest: AuthenticationRequest
    ): ResponseEntity<AuthenticationResponse> = ResponseEntity.ok(service.signIn(authenticationRequest))

    override fun signUp(registerRequest: RegisterRequest): ResponseEntity<EmailConfirmedResponse> {
        val response = service.signUp(registerRequest)
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    override fun verifyUser(token: String): ResponseEntity<EmailConfirmedResponse> {
        return ResponseEntity.ok(service.verifyUser(token))
    }
}