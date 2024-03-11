package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.error.UserNotFoundException
import com.onecosys.getthingsdone.user.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceImpl(private val repository: UserRepository) : AuthenticationService {
    override fun getCurrentAuthenticatedUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw UserNotFoundException("Authenticated user not found")

        val username = when (authentication.principal) {
            is UserDetails -> (authentication.principal as UserDetails).username
            else -> authentication.principal.toString()
        }

        return repository.findBy_username(username)
            ?: throw UserNotFoundException("User not found with username: $username")
    }
}
