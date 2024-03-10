package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.models.UserInfoResponse
import com.onecosys.getthingsdone.models.UserInfoUpdateRequest
import com.onecosys.getthingsdone.models.UserPasswordUpdateRequest

interface UserService {

    fun changeEmail(request: Map<String, String>): UserInfoResponse

    fun changeUsername(request: Map<String, String>): UserInfoResponse

    fun changePassword(request: UserPasswordUpdateRequest)

    fun changeInfo(request: UserInfoUpdateRequest): UserInfoResponse

    fun fetchInfo(): UserInfoResponse
}