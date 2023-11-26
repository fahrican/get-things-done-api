package com.onecosys.getthingsdone.task.util

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

internal class SpringSecurityContextProviderTest {

    private val mockSecurityContext = mockk<SecurityContext>(relaxed = true)
    private val provider = SpringSecurityContextProvider()


    @BeforeEach
    fun setUp() {
        mockkStatic(SecurityContextHolder::class) // Mocking the static method SecurityContextHolder.getContext()
        every { SecurityContextHolder.getContext() } returns mockSecurityContext
    }

    @AfterEach
    fun tearDown() {
        // Releasing the static mocks
        unmockkStatic(SecurityContextHolder::class)
    }

    @Test
    fun `when get current user authentication then expect current user authentication`() {
        val mockAuth = mockk<Authentication>(relaxed = true)
        every { mockSecurityContext.authentication } returns mockAuth

        val result = provider.getCurrentUserAuthentication()

        assertEquals(mockAuth, result)
    }

    @Test
    fun `getCurrentUserAuthentication should return null when Authentication is not set`() {
        every { mockSecurityContext.authentication } returns null

        val result = provider.getCurrentUserAuthentication()

        assertNull(result)
    }
}