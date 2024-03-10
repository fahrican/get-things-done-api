package com.onecosys.getthingsdone.user.util

import com.onecosys.getthingsdone.user.entity.User
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UserInfoMapperTest {

    @Test
    fun `when entity to dto is triggered then correct user info response`() {
        val user = mockk<User>()
        every { user.firstName } returns "John"
        every { user.lastName } returns "Doe"
        every { user.email } returns "john.doe@example.com"
        every { user.username } returns "johndoe"
        val userInfoMapper = UserInfoMapper()

        val userInfoResponse = userInfoMapper.toDto(user)

        assertEquals("John", userInfoResponse.firstName)
        assertEquals("Doe", userInfoResponse.lastName)
        assertEquals("john.doe@example.com", userInfoResponse.email)
        assertEquals("johndoe", userInfoResponse.username)
    }
}