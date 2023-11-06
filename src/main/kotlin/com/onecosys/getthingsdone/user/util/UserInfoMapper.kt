package com.onecosys.getthingsdone.user.util

import com.onecosys.getthingsdone.user.model.dto.UserInfoResponse
import com.onecosys.getthingsdone.user.model.entity.User
import org.springframework.stereotype.Component

@Component
class UserInfoMapper {

    fun toDto(entity: User) = UserInfoResponse(
        entity.id,
        entity.firstName,
        entity.lastName,
        entity.email
    )
}