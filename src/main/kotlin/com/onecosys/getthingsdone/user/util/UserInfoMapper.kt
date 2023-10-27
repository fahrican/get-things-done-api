package com.onecosys.getthingsdone.user.util

import com.onecosys.getthingsdone.user.dto.UserInfoResponse
import com.onecosys.getthingsdone.user.entity.User
import org.springframework.stereotype.Component

@Component
class UserInfoMapper {

    fun toDto(entity: User) = UserInfoResponse(
        entity.firstName,
        entity.lastName,
        entity.email
    )
}