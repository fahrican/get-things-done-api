package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.user.model.dto.UserInfoResponse
import com.onecosys.getthingsdone.user.model.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.user.model.dto.UserPasswordUpdateRequest
import java.security.Principal

interface UserService {

    fun changePassword(request: UserPasswordUpdateRequest, connectedUser: Principal)

    fun changeInfo(request: UserInfoUpdateRequest, connectedUser: Principal): UserInfoResponse
}