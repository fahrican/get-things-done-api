package com.onecosys.getthingsdone.user

import jakarta.persistence.Entity
import org.springframework.stereotype.Component

@Component
class UserInfoMapper {

    fun toDto(entity: User) = UserInfoResponse(
        entity.firstName,
        entity.lastName,
        entity.email
    )
}