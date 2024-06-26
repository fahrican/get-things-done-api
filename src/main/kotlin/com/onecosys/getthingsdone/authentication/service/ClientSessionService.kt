package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.user.entity.AppUser
import org.springframework.security.core.Authentication

interface ClientSessionService {

    fun retrieveAuthentication(): Authentication?

    fun findCurrentSessionUser(): AppUser

    fun getAuthenticatedUser(): AppUser
}