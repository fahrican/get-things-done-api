package com.onecosys.getthingsdone.user.rest.web

import com.onecosys.getthingsdone.apis.UserResource
import com.onecosys.getthingsdone.models.UserInfoResponse
import com.onecosys.getthingsdone.models.UserInfoUpdateRequest
import com.onecosys.getthingsdone.models.UserPasswordUpdateRequest
import com.onecosys.getthingsdone.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val service: UserService) : UserResource {

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
