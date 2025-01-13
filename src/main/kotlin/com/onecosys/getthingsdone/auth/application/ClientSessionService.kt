package com.onecosys.getthingsdone.auth.application

import com.onecosys.getthingsdone.auth.domain.AppUser
import org.springframework.modulith.ApplicationModule
import org.springframework.security.core.Authentication

@ApplicationModule(type = ApplicationModule.Type.OPEN)
interface ClientSessionService {

    fun retrieveAuthentication(): Authentication?

    fun findCurrentSessionUser(): AppUser

    fun getAuthenticatedUser(): AppUser
}