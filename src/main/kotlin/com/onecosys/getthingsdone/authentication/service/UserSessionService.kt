package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.user.entity.User
import org.springframework.security.core.Authentication

interface UserSessionService {

    fun retrieveAuthentication(): Authentication?

    fun findCurrentSessionUser(): User

    fun getAuthenticatedUser(): User
}