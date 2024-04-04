package com.onecosys.getthingsdone.user.util

import com.onecosys.getthingsdone.user.entity.AppUser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UserInfoMapperTest {

    @Test
    fun `when entity to dto is triggered then correct user info response`() {
        val appUser = mockk<AppUser>()
        every { appUser.firstName } returns "John"
        every { appUser.lastName } returns "Doe"
        every { appUser.email } returns "john.doe@example.com"
        every { appUser.username } returns "johndoe"
        val userInfoMapper = UserInfoMapper()

        val userInfoResponse = userInfoMapper.toDto(appUser)

        assertEquals("John", userInfoResponse.firstName)
        assertEquals("Doe", userInfoResponse.lastName)
        assertEquals("john.doe@example.com", userInfoResponse.email)
        assertEquals("johndoe", userInfoResponse.username)
    }
}