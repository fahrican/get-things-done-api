package com.onecosys.getthingsdone.user.web.rest

import com.onecosys.getthingsdone.api.UserResource
import com.onecosys.getthingsdone.dto.UserInfoResponse
import com.onecosys.getthingsdone.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.dto.UserPasswordUpdateRequest
import com.onecosys.getthingsdone.user.service.AppUserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AppUserController(private val service: AppUserService) : UserResource {

    override fun changeEmail(
        requestBody: Map<String, String>
    ): ResponseEntity<UserInfoResponse> = ResponseEntity.ok(service.changeEmail(requestBody))

    override fun changePassword(
        userPasswordUpdateRequest: UserPasswordUpdateRequest
    ) = ResponseEntity.ok(service.changePassword(userPasswordUpdateRequest))

    override fun changeUsername(
        requestBody: Map<String, String>
    ): ResponseEntity<UserInfoResponse> = ResponseEntity.ok(service.changeUsername(requestBody))

    override fun fetchInfo(): ResponseEntity<UserInfoResponse> = ResponseEntity.ok(service.fetchInfo())

    override fun updateInfo(
        userInfoUpdateRequest: UserInfoUpdateRequest
    ): ResponseEntity<UserInfoResponse> = ResponseEntity.ok(service.changeInfo(userInfoUpdateRequest))
}
