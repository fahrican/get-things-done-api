package com.onecosys.getthingsdone.user.util

import com.onecosys.getthingsdone.models.UserInfoResponse
import com.onecosys.getthingsdone.user.entity.User
import org.springframework.stereotype.Component

@Component
class UserInfoMapper {

    fun toDto(entity: User) = UserInfoResponse(
        firstName = entity.firstName,
        lastName = entity.lastName,
        email = entity.email,
        username = entity.username
    )
}