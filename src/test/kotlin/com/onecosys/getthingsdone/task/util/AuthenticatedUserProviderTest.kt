package com.onecosys.getthingsdone.task.util

import com.onecosys.getthingsdone.user.entity.User
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.security.core.Authentication


internal class AuthenticatedUserProviderTest {

    @Test
    fun `when get user is triggered the expect current authenticated user`() {
        val mockSecurityContextProvider = mockk<SecurityContextProvider>()
        val mockAuthentication = mockk<Authentication>()
        val expectedUser = mockk<User>()
        every { mockSecurityContextProvider.getCurrentUserAuthentication() } returns mockAuthentication
        every { mockAuthentication.principal } returns expectedUser
        val authenticatedUserProvider = AuthenticatedUserProvider(mockSecurityContextProvider)

        val result = authenticatedUserProvider.getUser()

        assertNotNull(result)
    }
}