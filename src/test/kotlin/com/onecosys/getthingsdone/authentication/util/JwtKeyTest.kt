package com.onecosys.getthingsdone.authentication.util

import com.onecosys.getthingsdone.error.JwtKeyException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class JwtKeyTest {

    private val keyGenerator = mockk<KeyGenerator>()
    private lateinit var jwtKey: JwtKey

    @BeforeEach
    fun setUp() {
        every { keyGenerator.generateHmacShaKey() } returns Keys.secretKeyFor(SignatureAlgorithm.HS256)
        jwtKey = JwtKey(keyGenerator)
    }

    @Test
    fun `when jwt secret key is called then should successfully generate JWT key`() {
        assertDoesNotThrow { val key = jwtKey.secretKey }
    }

    @Test
    fun `when generate key is called then should log error and throw JWT key exception on failure`() {
        every { keyGenerator.generateHmacShaKey() } throws IllegalStateException("expected exception")

        val exception = assertThrows<JwtKeyException> { jwtKey.secretKey }
        assertEquals("Invalid JWT secret key", exception.message)
    }
}