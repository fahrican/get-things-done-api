package com.onecosys.getthingsdone.user

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.Principal

@Service
class UserService(private val passwordEncoder: PasswordEncoder, private val repository: UserRepository) {

    fun changePassword(request: PasswordChangeRequest, connectedUser: Principal) {

        val user = (connectedUser as UsernamePasswordAuthenticationToken).principal as User

        if (!passwordEncoder.matches(request.currentPassword, user.password)) {
            throw IllegalStateException("The current password is wrong!")
        }

        if (request.newPassword != request.newPasswordConfirmation) {
            throw IllegalStateException("Your new password does not match with the password confirmation!")
        }

        user._password = passwordEncoder.encode(request.newPassword)
        repository.save(user)
    }
}