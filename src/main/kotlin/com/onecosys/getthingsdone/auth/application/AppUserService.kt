package com.onecosys.getthingsdone.auth.application

import com.onecosys.getthingsdone.dto.UserInfoResponse
import com.onecosys.getthingsdone.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.dto.UserPasswordUpdateRequest

interface AppUserService {

    fun changeEmail(request: Map<String, String>): UserInfoResponse

    fun changeUsername(request: Map<String, String>): UserInfoResponse

    fun changePassword(request: UserPasswordUpdateRequest)

    fun changeInfo(request: UserInfoUpdateRequest): UserInfoResponse

    fun fetchInfo(): UserInfoResponse
}