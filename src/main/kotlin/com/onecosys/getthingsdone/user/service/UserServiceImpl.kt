package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.error.BadRequestException
import com.onecosys.getthingsdone.error.PasswordMismatchException
import com.onecosys.getthingsdone.error.UserNotFoundException
import com.onecosys.getthingsdone.models.UserInfoResponse
import com.onecosys.getthingsdone.models.UserInfoUpdateRequest
import com.onecosys.getthingsdone.models.UserPasswordUpdateRequest
import com.onecosys.getthingsdone.user.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import com.onecosys.getthingsdone.user.util.UserInfoMapper
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserServiceImpl(
    private val passwordEncoder: PasswordEncoder,
    private val repository: UserRepository,
    private val mapper: UserInfoMapper
) : UserService {

    override fun changeEmail(request: Map<String, String>): UserInfoResponse {
        val currentUser = getCurrentUser()

        val newEmail = request["email"] ?: throw BadRequestException("Email is missing in request")
        validateEmail(newEmail)

        currentUser.email = newEmail
        val updatedUser = repository.save(currentUser)

        return mapper.toDto(updatedUser)
    }

    override fun changeUsername(request: Map<String, String>): UserInfoResponse {
        val user = getCurrentUser()

        if (request["username"] != null || request["username"] != "") {
            validateUsername(request["username"].toString())
            user._username = request["username"].toString()

            val updatedUser = repository.save(user)
            return mapper.toDto(updatedUser)
        } else throw BadRequestException("Username can't be blank/null !")
    }

    override fun changePassword(request: UserPasswordUpdateRequest) {
        val user = getCurrentUser()

        if (!passwordEncoder.matches(request.currentPassword, user.password)) {
            throw PasswordMismatchException("The current password is wrong!")
        }

        if (request.newPassword != request.newPasswordConfirmation) {
            throw PasswordMismatchException("Your new password does not match with the password confirmation!")
        }

        user._password = passwordEncoder.encode(request.newPassword)
        repository.save(user)
    }

    override fun changeInfo(request: UserInfoUpdateRequest): UserInfoResponse {
        val user = getCurrentUser()

        user.apply {
            firstName = request.firstName ?: firstName
            lastName = request.lastName ?: lastName
        }

        val savedUser: User = repository.save(user)
        return mapper.toDto(savedUser)
    }

    override fun fetchInfo(): UserInfoResponse {
        val user = getCurrentUser()
        return mapper.toDto(user)
    }

    fun validateEmail(email: String) {
        if (!email.contains("@")) {
            throw BadRequestException("Email must contain '@' symbol")
        }

        if (repository.findByEmail(email) != null) {
            throw BadRequestException("Email is already used by another user")
        }
    }

    fun validateUsername(username: String) {
        if (username.contains("@")) {
            throw BadRequestException("Username cannot contain '@' symbol")
        }

        if (repository.findBy_username(username) != null) {
            throw BadRequestException("Username is already used by another user")
        }
    }

    private fun getCurrentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw UserNotFoundException("Authenticated user not found")

        val principal = authentication.principal as? User
            ?: throw IllegalStateException("Authentication principal is not a User object")

        return principal
    }
}