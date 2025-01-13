package com.onecosys.getthingsdone.auth.application

import com.onecosys.getthingsdone.auth.domain.AppUser
import com.onecosys.getthingsdone.auth.domain.BadUserRequestException
import com.onecosys.getthingsdone.auth.domain.PasswordMismatchException
import com.onecosys.getthingsdone.auth.infrastructure.AppUserRepository
import com.onecosys.getthingsdone.dto.UserInfoResponse
import com.onecosys.getthingsdone.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.dto.UserPasswordUpdateRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AppUserServiceImpl(
    private val passwordEncoder: PasswordEncoder,
    private val repository: AppUserRepository,
    private val mapper: UserInfoMapper,
    private val service: ClientSessionService
) : AppUserService {

    override fun changeEmail(request: Map<String, String>): UserInfoResponse {
        val currentUser = service.findCurrentSessionUser()

        val newEmail = request["email"] ?: throw BadUserRequestException("Email is missing in request")
        validateEmail(newEmail)

        currentUser.email = newEmail
        val updatedUser = repository.save(currentUser)

        return mapper.toDto(updatedUser)
    }

    override fun changeUsername(request: Map<String, String>): UserInfoResponse {
        val user = service.findCurrentSessionUser()

        val newUsername = request["username"] ?: throw BadUserRequestException("Username can't be blank/null !")
        validateUsername(newUsername)
        user.appUsername = newUsername

        val updatedUser = repository.save(user)
        return mapper.toDto(updatedUser)
    }

    override fun changePassword(request: UserPasswordUpdateRequest) {
        val user = service.findCurrentSessionUser()

        if (!passwordEncoder.matches(request.currentPassword, user.password)) {
            throw PasswordMismatchException("The current password is wrong!")
        }

        if (request.newPassword != request.newPasswordConfirmation) {
            throw PasswordMismatchException("Your new password does not match with the password confirmation!")
        }

        user.appPassword = passwordEncoder.encode(request.newPassword)
        repository.save(user)
    }

    override fun changeInfo(request: UserInfoUpdateRequest): UserInfoResponse {
        val user = service.findCurrentSessionUser()

        user.apply {
            firstName = request.firstName ?: firstName
            lastName = request.lastName ?: lastName
        }

        val savedAppUser: AppUser = repository.save(user)
        return mapper.toDto(savedAppUser)
    }

    override fun fetchInfo(): UserInfoResponse {
        val user = service.findCurrentSessionUser()
        return mapper.toDto(user)
    }

    fun validateEmail(email: String, currentUserId: Long? = null) {
        if (!email.contains("@")) {
            throw BadUserRequestException("Email must contain '@' symbol")
        }

        repository.findByEmail(email)?.let {
            if (currentUserId == null || it.id != currentUserId) {
                throw BadUserRequestException("Email is already used by another user")
            }
        }
    }

    fun validateUsername(username: String) {
        if (username.contains("@")) {
            throw BadUserRequestException("Username cannot contain '@' symbol")
        }

        if (repository.findByAppUsername(username) != null) {
            throw BadUserRequestException("Username is already used by another user")
        }
    }
}