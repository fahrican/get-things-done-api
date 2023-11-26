package com.onecosys.getthingsdone.authentication.util

import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder

internal class UserRegistrationMapperTest {

    @Test
    fun `when request to entity is triggered then expect correct user entity`() {
        val passwordEncoder = mockk<PasswordEncoder>()
        val request = RegisterRequest(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            username = "johndoe",
            password = "password",
            passwordConfirmation = "password"
        )
        val encodedPassword = "encodedPassword"
        every { passwordEncoder.encode(request.password) } returns encodedPassword
        val userRegistrationMapper = UserRegistrationMapper()

        val user = userRegistrationMapper.toEntity(request, passwordEncoder)

        assertEquals(request.firstName, user.firstName)
        assertEquals(request.lastName, user.lastName)
        assertEquals(request.email, user.email)
        assertEquals(request.username, user._username)
        assertEquals(encodedPassword, user._password)
    }
}