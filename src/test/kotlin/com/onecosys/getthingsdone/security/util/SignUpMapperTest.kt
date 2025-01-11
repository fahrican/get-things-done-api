package com.onecosys.getthingsdone.security.util

import com.onecosys.getthingsdone.dto.RegisterRequest
import com.onecosys.getthingsdone.security.application.SignUpMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder

internal class SignUpMapperTest {

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
        val signUpMapper = SignUpMapper()

        val user = signUpMapper.toEntity(request, passwordEncoder)

        assertEquals(request.firstName, user.firstName)
        assertEquals(request.lastName, user.lastName)
        assertEquals(request.email, user.email)
        assertEquals(request.username, user.appUsername)
        assertEquals(encodedPassword, user.appPassword)
    }
}