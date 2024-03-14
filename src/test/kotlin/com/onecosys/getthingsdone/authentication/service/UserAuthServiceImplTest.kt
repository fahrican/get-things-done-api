package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.error.UserNotFoundException
import com.onecosys.getthingsdone.user.entity.User
import com.onecosys.getthingsdone.user.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

@ExtendWith(MockKExtension::class)
class UserAuthServiceImplTest {

    @RelaxedMockK
    private lateinit var mockSecurityContext: SecurityContext

    @RelaxedMockK
    private lateinit var mockAuthentication: Authentication

    @RelaxedMockK
    private lateinit var mockUserRepository: UserRepository

    private lateinit var objectUnderTest: UserAuthService

    val user = User();

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { mockSecurityContext.authentication } returns mockAuthentication
        SecurityContextHolder.setContext(mockSecurityContext)
        objectUnderTest = UserAuthServiceImpl(mockUserRepository)
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `when get current user authentication is called then return mock authentication`() {
        val result = objectUnderTest.getCurrentUserAuthentication()

        assertEquals(mockAuthentication, result, "The authentication returned was not as expected")
    }

    @Test
    fun `when get user is called then return expected user`() {
        every { mockAuthentication.principal } returns user

        val result = objectUnderTest.getUser()

        assertEquals(user, result)
    }

    @Test
    fun `when get current authenticated user is called then expect user not found`() {
        val mockException = mockk<UserNotFoundException>()
        every { mockSecurityContext.authentication } returns null
        every { mockAuthentication.principal } returns mockException

        val actualResult = assertThrows<UserNotFoundException> { objectUnderTest.getCurrentAuthenticatedUser() }

        assertEquals("Authenticated user not found", actualResult.message)
    }

    @Test
    fun `when get current authenticated user is called then expect user return`() {
       val username = "Salah Ad-Din"
        every { mockUserRepository.findBy_username(any()) } returns user

        val actualResult = objectUnderTest.getCurrentAuthenticatedUser()

        assertEquals(user, actualResult)
    }
}