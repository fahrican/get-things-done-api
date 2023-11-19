package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.util.JwtKey
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
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
    private lateinit var jwtKey: JwtKey

    private lateinit var secretKey: SecretKey
    private val username = "testUser"

    private lateinit var jwtService: JwtService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512) // or load from a secure location

        jwtService = JwtServiceImpl(jwtKey)

        every { jwtKey.secretKey } returns secretKey
    }

    @Test
    fun `extractUsername should return the correct username from token`() {
        // Create a token with the subject claim set to the username
        val token = Jwts.builder()
            .setSubject(username)
            .signWith(secretKey)
            .compact()

        // Call the method under test
        val extractedUsername = jwtService.extractUsername(token)

        // Assert the extracted username is as expected
        assertEquals(username, extractedUsername)
    }
}