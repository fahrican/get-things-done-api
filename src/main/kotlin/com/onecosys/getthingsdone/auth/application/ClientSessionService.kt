package com.onecosys.getthingsdone.auth.application

import com.onecosys.getthingsdone.auth.domain.AppUser
import org.springframework.security.core.Authentication

interface ClientSessionService {

    fun retrieveAuthentication(): Authentication?

    fun findCurrentSessionUser(): AppUser

    fun getAuthenticatedUser(): AppUser
}