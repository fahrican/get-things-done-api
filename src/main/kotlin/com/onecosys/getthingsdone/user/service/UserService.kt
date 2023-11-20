package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.user.model.dto.UserInfoResponse
import com.onecosys.getthingsdone.user.model.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.user.model.dto.UserPasswordUpdateRequest
import java.security.Principal

interface UserService {

    fun changeEmail(request: HashMap<String, String>, connectedUser: Principal): UserInfoResponse

    fun changeUsername(request: HashMap<String, String>, connectedUser: Principal): UserInfoResponse

    fun changePassword(request: UserPasswordUpdateRequest, connectedUser: Principal)

    fun changeInfo(request: UserInfoUpdateRequest, connectedUser: Principal): UserInfoResponse

    fun fetchInfo(connectedUser: Principal): UserInfoResponse
}