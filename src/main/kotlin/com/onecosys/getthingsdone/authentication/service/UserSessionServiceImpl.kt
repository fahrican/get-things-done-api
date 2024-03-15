package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.error.UserNotFoundException
import com.onecosys.getthingsdone.user.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class UserSessionServiceImpl(private val repository: UserRepository) : UserSessionService {

    override fun retrieveAuthentication(): Authentication? {
        return SecurityContextHolder.getContext().authentication
    }

    override fun findCurrentSessionUser(): User {
        val authentication = retrieveAuthentication()
            ?: throw UserNotFoundException("Authenticated user not found")

        val username = when (val principal = authentication.principal) {
            is UserDetails -> principal.username
            else -> principal.toString()
        }

        return repository.findBy_username(username)
            ?: throw UserNotFoundException("User not found with username: $username")
    }

    override fun getAuthenticatedUser(): User {
        val authentication = retrieveAuthentication()
        return authentication?.principal as User
    }
}