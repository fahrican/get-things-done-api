package com.onecosys.getthingsdone.task.util

import com.onecosys.getthingsdone.user.model.entity.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component


interface SecurityContextProvider {

    fun getCurrentUserAuthentication(): Authentication?
}

@Component
class SpringSecurityContextProvider : SecurityContextProvider {

    override fun getCurrentUserAuthentication(): Authentication? {
        return SecurityContextHolder.getContext().authentication
    }
}

@Component
class AuthenticatedUserProvider(private val securityContextProvider: SecurityContextProvider) {

    fun getUser(): User {
        val authentication = securityContextProvider.getCurrentUserAuthentication()
        return authentication?.principal as User
    }
}