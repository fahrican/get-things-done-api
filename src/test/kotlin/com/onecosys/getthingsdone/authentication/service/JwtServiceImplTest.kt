package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.util.JwtKey
import io.jsonwebtoken.Jwts
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import javax.crypto.SecretKey

@ExtendWith(MockKExtension::class)
internal class JwtServiceImplTest {

    @RelaxedMockK
    private lateinit var mockJwtKey: JwtKey

    private lateinit var secretKey: SecretKey

    private val username = "testUser"

    private lateinit var objectUnderTest: JwtService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        secretKey = Jwts.SIG.HS256.key().build()

        objectUnderTest = JwtServiceImpl(mockJwtKey)

        every { mockJwtKey.secretKey } returns secretKey
    }

    @Test
    fun `when extract username is triggered then expect correct username`() {
        val token = Jwts.builder()
            .claim("sub", username)
            .signWith(secretKey)
            .compact()

        val actualUsername = objectUnderTest.extractUsername(token)

        assertEquals(username, actualUsername)
    }
}