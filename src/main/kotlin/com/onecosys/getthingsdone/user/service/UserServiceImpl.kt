package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.error.BadRequestException
import com.onecosys.getthingsdone.error.PasswordMismatchException
import com.onecosys.getthingsdone.user.model.dto.UserInfoResponse
import com.onecosys.getthingsdone.user.model.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.user.model.dto.UserPasswordUpdateRequest
import com.onecosys.getthingsdone.user.model.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import com.onecosys.getthingsdone.user.util.UserInfoMapper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.Principal


@Service
class UserServiceImpl(
    private val passwordEncoder: PasswordEncoder,
    private val repository: UserRepository,
    private val mapper: UserInfoMapper
) : UserService {


    override fun changeEmail(request: UserInfoUpdateRequest, connectedUser: Principal): UserInfoResponse {
        val user = (connectedUser as UsernamePasswordAuthenticationToken).principal as User

        request.email?.let {
            if (!request.email.contains("@")) {
                throw BadRequestException("Email does not contain @ symbol")
            }

            repository.findByEmail(request.email)?.let {
                throw BadRequestException("Email is already used by another user")
            }

            user.email = request.email
            val savedUser: User = repository.save(user)
            return mapper.toDto(savedUser)
        } ?: run { throw BadRequestException("Email can't be blank!") }
    }

    override fun changeUsername(request: UserInfoUpdateRequest, connectedUser: Principal): UserInfoResponse {
        val user = (connectedUser as UsernamePasswordAuthenticationToken).principal as User

        request.username?.let {
            if (request.username.contains("@")) {
                throw BadRequestException("Username is not an email it can't contain @ symbol")
            }

            repository.findBy_username(request.username)?.let {
                throw BadRequestException("Username is already used by another user")
            }

            user._username = request.username
            val savedUser: User = repository.save(user)
            return mapper.toDto(savedUser)
        } ?: run { throw BadRequestException("Username can't be blank!") }
    }

    override fun changePassword(request: UserPasswordUpdateRequest, connectedUser: Principal) {
        val user = (connectedUser as UsernamePasswordAuthenticationToken).principal as User

        if (!passwordEncoder.matches(request.currentPassword, user.password)) {
            throw PasswordMismatchException("The current password is wrong!")
        }

        if (request.newPassword != request.newPasswordConfirmation) {
            throw PasswordMismatchException("Your new password does not match with the password confirmation!")
        }

        user._password = passwordEncoder.encode(request.newPassword)
        repository.save(user)
    }

    override fun changeInfo(request: UserInfoUpdateRequest, connectedUser: Principal): UserInfoResponse {
        val user = (connectedUser as UsernamePasswordAuthenticationToken).principal as User

        user.apply {
            email = request.email ?: email
            firstName = request.firstName ?: firstName
            lastName = request.lastName ?: lastName
        }

        val savedUser: User = repository.save(user)
        return mapper.toDto(savedUser)
    }
}