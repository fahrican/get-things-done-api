package com.onecosys.getthingsdone.auth.application

import com.onecosys.getthingsdone.auth.domain.AppUser
import com.onecosys.getthingsdone.dto.UserInfoResponse
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