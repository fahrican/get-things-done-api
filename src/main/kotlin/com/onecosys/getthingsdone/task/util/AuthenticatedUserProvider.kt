package com.onecosys.getthingsdone.task.util

import com.onecosys.getthingsdone.user.model.entity.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthenticatedUserProvider {

    fun getUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.principal as User
    }
}