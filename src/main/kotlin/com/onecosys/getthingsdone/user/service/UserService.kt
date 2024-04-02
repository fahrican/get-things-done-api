package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.dto.UserInfoResponse
import com.onecosys.getthingsdone.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.dto.UserPasswordUpdateRequest

interface UserService {

    fun changeEmail(request: Map<String, String>): UserInfoResponse

    fun changeUsername(request: Map<String, String>): UserInfoResponse

    fun changePassword(request: UserPasswordUpdateRequest)

    fun changeInfo(request: UserInfoUpdateRequest): UserInfoResponse

    fun fetchInfo(): UserInfoResponse
}