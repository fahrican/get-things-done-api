package com.onecosys.getthingsdone.user.rest

import com.onecosys.getthingsdone.user.dto.UserInfoResponse
import com.onecosys.getthingsdone.user.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.user.dto.UserPasswordUpdateRequest
import com.onecosys.getthingsdone.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@RestController
@RequestMapping("api/v1/users")
class UserController(private val service: UserService) {

    @Operation(summary = "change user password", tags = ["user"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Updating the password was successful",
                content = [Content(
                    mediaType = "application/json",
                )]
            )
        ]
    )
    @PatchMapping("password")
    fun changePassword(
        @Valid @RequestBody request: UserPasswordUpdateRequest,
        connectedUser: Principal
    ) = ResponseEntity.ok(service.changePassword(request, connectedUser))

    @Operation(summary = "update user information", tags = ["user"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Updating information was successful",
                content = [Content(
                    mediaType = "application/json",
                )]
            )
        ]
    )
    @PatchMapping("info")
    fun updateInfo(
        @Valid @RequestBody request: UserInfoUpdateRequest,
        connectedUser: Principal
    ): ResponseEntity<UserInfoResponse> = ResponseEntity.ok(service.changeInfo(request, connectedUser))
}