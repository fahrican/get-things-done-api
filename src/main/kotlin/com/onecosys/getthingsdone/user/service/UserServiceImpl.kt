package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.authentication.service.UserAuthService
import com.onecosys.getthingsdone.error.BadRequestException
import com.onecosys.getthingsdone.error.PasswordMismatchException
import com.onecosys.getthingsdone.models.UserInfoResponse
import com.onecosys.getthingsdone.models.UserInfoUpdateRequest
import com.onecosys.getthingsdone.models.UserPasswordUpdateRequest
import com.onecosys.getthingsdone.user.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import com.onecosys.getthingsdone.user.util.UserInfoMapper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserServiceImpl(
    private val passwordEncoder: PasswordEncoder,
    private val repository: UserRepository,
    private val mapper: UserInfoMapper,
    private val userAuthService: UserAuthService
) : UserService {

    override fun changeEmail(request: Map<String, String>): UserInfoResponse {
        val currentUser = userAuthService.findCurrentSessionUser()

        val newEmail = request["email"] ?: throw BadRequestException("Email is missing in request")
        validateEmail(newEmail)

        currentUser.email = newEmail
        val updatedUser = repository.save(currentUser)

        return mapper.toDto(updatedUser)
    }

    override fun changeUsername(request: Map<String, String>): UserInfoResponse {
        val user = userAuthService.findCurrentSessionUser()

        val newUsername = request["username"] ?: throw BadRequestException("Username can't be blank/null !")
        validateUsername(newUsername)
        user._username = newUsername

        val updatedUser = repository.save(user)
        return mapper.toDto(updatedUser)
    }

    override fun changePassword(request: UserPasswordUpdateRequest) {
        val user = userAuthService.findCurrentSessionUser()

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
        val user = userAuthService.findCurrentSessionUser()

        user.apply {
            firstName = request.firstName ?: firstName
            lastName = request.lastName ?: lastName
        }

        val savedUser: User = repository.save(user)
        return mapper.toDto(savedUser)
    }

    override fun fetchInfo(): UserInfoResponse {
        val user = userAuthService.findCurrentSessionUser()
        return mapper.toDto(user)
    }

    fun validateEmail(email: String, currentUserId: Long? = null) {
        if (!email.contains("@")) {
            throw BadRequestException("Email must contain '@' symbol")
        }

        repository.findByEmail(email)?.let {
            if (currentUserId == null || it.id != currentUserId) {
                throw BadRequestException("Email is already used by another user")
            }
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
}