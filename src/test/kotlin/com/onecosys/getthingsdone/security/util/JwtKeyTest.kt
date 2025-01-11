package com.onecosys.getthingsdone.security.util

import com.onecosys.getthingsdone.security.infrastructure.JwtKey
import com.onecosys.getthingsdone.security.infrastructure.KeyGenerator
import com.onecosys.getthingsdone.shared.error.JwtKeyException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.crypto.SecretKey


internal class JwtKeyTest {

    private val mockSecretKey = mockk<SecretKey>(relaxed = true)
    private val mockKeyGenerator = mockk<KeyGenerator>()
    private lateinit var jwtKey: JwtKey

    @BeforeEach
    fun setUp() {
        every { mockKeyGenerator.generateHmacShaKey() } returns mockSecretKey
        jwtKey = JwtKey(mockKeyGenerator)
    }

    @Test
    fun `when jwt secret key is called then should successfully generate JWT key`() {
        val key = assertDoesNotThrow { jwtKey.secretKey }

        assertNotNull(key)
    }

    @Test
    fun `when generate key is called then should log error and throw JWT key exception on failure`() {
        every { mockKeyGenerator.generateHmacShaKey() } throws IllegalStateException("expected exception")

        val exception = assertThrows<JwtKeyException> { jwtKey.secretKey }

        assertEquals("Invalid JWT secret key", exception.message)
    }
}