package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.user.dto.UserInfoResponse
import com.onecosys.getthingsdone.user.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.user.dto.UserPasswordUpdateRequest
import java.security.Principal

interface UserService {

    fun changePassword(id: Long, request: UserPasswordUpdateRequest, connectedUser: Principal)

    fun changeInfo(id: Long, request: UserInfoUpdateRequest, connectedUser: Principal): UserInfoResponse
}