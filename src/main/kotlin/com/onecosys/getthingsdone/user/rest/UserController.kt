package com.onecosys.getthingsdone.user.rest

import com.onecosys.getthingsdone.apis.UserResource
import com.onecosys.getthingsdone.models.UserInfoResponse
import com.onecosys.getthingsdone.models.UserPasswordUpdateRequest
import com.onecosys.getthingsdone.user.service.UserService
import org.springframework.http.ResponseEntity


class UserController(private val service: UserService) : UserResource {

    override fun changeEmail(
        requestBody: Map<String, String>
    ): ResponseEntity<UserInfoResponse> = ResponseEntity.ok(service.changeEmail(requestBody))

    override fun changePassword(
        userPasswordUpdateRequest: UserPasswordUpdateRequest
    ) = ResponseEntity.ok(service.changePassword(userPasswordUpdateRequest))
}
