package com.onecosys.getthingsdone.user.util

import com.onecosys.getthingsdone.dto.UserInfoResponse
import com.onecosys.getthingsdone.user.entity.AppUser
import org.springframework.stereotype.Component

@Component
class UserInfoMapper {

    fun toDto(entity: AppUser) = UserInfoResponse(
        firstName = entity.firstName,
        lastName = entity.lastName,
        email = entity.email,
        username = entity.username
    )
}