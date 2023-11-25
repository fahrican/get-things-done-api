package com.onecosys.getthingsdone.authentication.util

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class JwtKeyTest {

    @Test
    fun `JwtKey should correctly decode a valid secret key`() {
        val mockJwtConfig = mockk<JwtConfig>()
        val validBase64Key = "c29tZSBzZWNyZXQga2V5IGVuY29kZWQgaW4gYmFzZTY0" // This should be a valid base64 string
        every { mockJwtConfig.secretKey } returns validBase64Key
        val jwtKey = JwtKey(mockJwtConfig)

        assertNotNull(jwtKey.secretKey)
        assertEquals("HmacSHA256", jwtKey.secretKey.algorithm)
    }

    @Test
    fun `JwtKey should throw an exception for an invalid secret key`() {
        val mockJwtKey = mockk<JwtKey>()
        every { mockJwtKey.secretKey } throws IllegalStateException("Invalid JWT secret key")

        val exception = assertThrows<IllegalStateException> { mockJwtKey.secretKey }

        assertEquals(exception.message, "Invalid JWT secret key")
    }
}