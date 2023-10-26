package com.onecosys.getthingsdone.user

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.Principal
import kotlin.reflect.full.memberProperties
import java.lang.reflect.Field
import org.springframework.util.ReflectionUtils


@Service
class UserService(
    private val passwordEncoder: PasswordEncoder,
    private val repository: UserRepository,
    private val mapper: UserInfoMapper
) {

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

    fun changeInfo(request: InfoChangeRequest, connectedUser: Principal): UserInfoResponse {
        val user = (connectedUser as UsernamePasswordAuthenticationToken).principal as User

        for (prop in InfoChangeRequest::class.memberProperties) {
            if (prop.get(request) != null) {
                val field: Field? = ReflectionUtils.findField(User::class.java, prop.name)
                field?.let {
                    it.isAccessible = true
                    ReflectionUtils.setField(it, user, prop.get(request))
                }
            }
        }

        val savedUser: User = repository.save(user)
        return mapper.toDto(savedUser)
    }
}