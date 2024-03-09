package com.onecosys.getthingsdone.authentication.web.rest

import com.onecosys.getthingsdone.apis.AuthenticationResource
import com.onecosys.getthingsdone.authentication.service.AuthenticationService
import com.onecosys.getthingsdone.models.AuthenticationRequest
import com.onecosys.getthingsdone.models.AuthenticationResponse
import com.onecosys.getthingsdone.models.EmailConfirmedResponse
import com.onecosys.getthingsdone.models.RegisterRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/auth")
class AuthenticationController(
    private val service: AuthenticationService
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