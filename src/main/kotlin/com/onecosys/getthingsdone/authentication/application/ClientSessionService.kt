package com.onecosys.getthingsdone.authentication.application

import com.onecosys.getthingsdone.user.domain.AppUser
import org.springframework.security.core.Authentication

interface ClientSessionService {

    fun retrieveAuthentication(): Authentication?

    fun findCurrentSessionUser(): AppUser

    fun getAuthenticatedUser(): AppUser
}