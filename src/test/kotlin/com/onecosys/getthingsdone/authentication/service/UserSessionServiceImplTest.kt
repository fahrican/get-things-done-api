package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.error.UserNotFoundException
import com.onecosys.getthingsdone.user.entity.AppUser
import com.onecosys.getthingsdone.user.repository.AppUserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

@ExtendWith(MockKExtension::class)
class UserSessionServiceImplTest {

    @RelaxedMockK
    private lateinit var mockSecurityContext: SecurityContext

    @RelaxedMockK
    private lateinit var mockAuthentication: Authentication

    @RelaxedMockK
    private lateinit var mockAppUserRepository: AppUserRepository

    private lateinit var objectUnderTest: UserSessionService

    val appUser = AppUser();

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { mockSecurityContext.authentication } returns mockAuthentication
        SecurityContextHolder.setContext(mockSecurityContext)
        objectUnderTest = UserSessionServiceImpl(mockAppUserRepository)
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `when retrieve authentication is called then return mock authentication`() {
        val result = objectUnderTest.retrieveAuthentication()

        assertEquals(mockAuthentication, result, "The authentication returned was not as expected")
    }

    @Test
    fun `when get authenticated user is called then return expected user`() {
        every { mockAuthentication.principal } returns appUser

        val result = objectUnderTest.getAuthenticatedUser()

        assertEquals(appUser, result)
    }

    @Test
    fun `when find current session user is called then expect user not found exception`() {
        val mockException = mockk<UserNotFoundException>()
        every { mockSecurityContext.authentication } returns null
        every { mockAuthentication.principal } returns mockException

        val actualResult = assertThrows<UserNotFoundException> { objectUnderTest.findCurrentSessionUser() }

        assertEquals("Authenticated user not found", actualResult.message)
    }

    @Test
    fun `when find current session user is called then expect user not found with username exception`() {
        val username = "salah-ad-din"
        val userDetails = mockk<UserDetails>()
        every { userDetails.username } returns username
        every { mockAuthentication.principal } returns userDetails
        every { mockAppUserRepository.findByAppUsername(username) } returns null

        val actualResult = assertThrows<UserNotFoundException> { objectUnderTest.findCurrentSessionUser() }

        assertEquals("User not found with username: $username", actualResult.message)
    }

    @Test
    fun `when find current session user is called then return expected user`() {
        every { mockAppUserRepository.findByAppUsername(any()) } returns appUser

        val actualResult = objectUnderTest.findCurrentSessionUser()

        assertEquals(appUser, actualResult)
    }
}