package com.onecosys.getthingsdone.authentication.web.rest

import com.onecosys.getthingsdone.authentication.dto.AuthenticationRequest
import com.onecosys.getthingsdone.authentication.dto.AuthenticationResponse
import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.authentication.service.AuthenticationService
import com.onecosys.getthingsdone.error.ApiError
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/auth")
class AuthenticationController(
    private val service: AuthenticationService
) {

    @Operation(summary = "sign-up user", tags = ["authentication"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "sign-up was successful",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = AuthenticationResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "409", description = "Invalid sign-up", content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ApiError::class)
                )]
            ),
        ]
    )
    @PostMapping("sign-up")
    fun signUp(@RequestBody request: RegisterRequest): ResponseEntity<String> =
        ResponseEntity.ok(service.signUp(request))


    @Operation(summary = "verify user", tags = ["authentication"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "email verification was successful",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = String::class)
                )]
            ),
            ApiResponse(
                responseCode = "403", description = "Verification step not complete", content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ApiError::class)
                )]
            ),
        ]
    )
    @GetMapping("verify")
    fun verifyUser(@RequestParam("token") token: String): ResponseEntity<String> {
        return ResponseEntity.ok(service.verifyUser(token))
    }


    @Operation(summary = "sign-in user", tags = ["authentication"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "sign-in was successful",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = AuthenticationResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "401", description = "Invalid sign-in", content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ApiError::class)
                )]
            ),
        ]
    )
    @PostMapping("sign-in")
    fun signIn(@RequestBody request: AuthenticationRequest): ResponseEntity<AuthenticationResponse> =
        ResponseEntity.ok(service.signIn(request))
}