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

    override fun changeEmail(request: HashMap<String, String>, connectedUser: Principal): UserInfoResponse {
        val user = (connectedUser as UsernamePasswordAuthenticationToken).principal as User

        if (request["email"] != null || request["email"] != "") {
            validateEmail(request["email"].toString())
            user.email = request["email"].toString()

            val updatedUser = repository.save(user)
            return mapper.toDto(updatedUser)
        } else throw BadRequestException("Email can't be blank/null !")
    }

    override fun changeUsername(request: HashMap<String, String>, connectedUser: Principal): UserInfoResponse {
        val user = (connectedUser as UsernamePasswordAuthenticationToken).principal as User

        if (request["username"] != null || request["username"] != "") {
            validateUsername(request["username"].toString())
            user._username = request["username"].toString()

            val updatedUser = repository.save(user)
            return mapper.toDto(updatedUser)
        } else throw BadRequestException("Username can't be blank/null !")
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
            firstName = request.firstName ?: firstName
            lastName = request.lastName ?: lastName
        }

        val savedUser: User = repository.save(user)
        return mapper.toDto(savedUser)
    }

    override fun fetchInfo(connectedUser: Principal): UserInfoResponse {
        val user = (connectedUser as UsernamePasswordAuthenticationToken).principal as User
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
}