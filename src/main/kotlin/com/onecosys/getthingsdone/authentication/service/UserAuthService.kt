package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.user.entity.User
import org.springframework.security.core.Authentication

interface UserAuthService {

    fun getCurrentUserAuthentication(): Authentication?

    fun getCurrentAuthenticatedUser(): User

    fun getUser(): User
}