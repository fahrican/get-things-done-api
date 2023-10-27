package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.user.dto.UserInfoResponse
import com.onecosys.getthingsdone.user.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.user.dto.UserPasswordUpdateRequest
import com.onecosys.getthingsdone.user.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import com.onecosys.getthingsdone.user.util.UserInfoMapper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.Principal
import kotlin.reflect.full.memberProperties
import java.lang.reflect.Field
import org.springframework.util.ReflectionUtils


@Service
class UserServiceImpl(
    private val passwordEncoder: PasswordEncoder,
    private val repository: UserRepository,
    private val mapper: UserInfoMapper
) : UserService {

    override fun changePassword(id: Long, request: UserPasswordUpdateRequest, connectedUser: Principal) {

        val user = (connectedUser as UsernamePasswordAuthenticationToken).principal as User

        check(id != user.id) {
            throw IllegalStateException("Use ID does not match ID of logged in user!")
        }

        check(!passwordEncoder.matches(request.currentPassword, user.password)) {
            throw IllegalStateException("The current password is wrong!")
        }

        check(request.newPassword != request.newPasswordConfirmation) {
            throw IllegalStateException("Your new password does not match with the password confirmation!")
        }

        user._password = passwordEncoder.encode(request.newPassword)
        repository.save(user)
    }

    override fun changeInfo(id: Long, request: UserInfoUpdateRequest, connectedUser: Principal): UserInfoResponse {
        val user = (connectedUser as UsernamePasswordAuthenticationToken).principal as User

        check(id != user.id) {
            throw IllegalStateException("Use ID does not match ID of logged in user!")
        }

        for (prop in UserInfoUpdateRequest::class.memberProperties) {
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