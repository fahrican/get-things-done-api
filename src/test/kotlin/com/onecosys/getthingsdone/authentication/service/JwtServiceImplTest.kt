package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.util.JwtKey
import com.onecosys.getthingsdone.error.JwtAuthenticationException
import io.jsonwebtoken.Jwts
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.core.userdetails.UserDetails
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey

@ExtendWith(MockKExtension::class)
internal class JwtServiceImplTest {

    @RelaxedMockK
    private lateinit var mockJwtKey: JwtKey

    private lateinit var secretKey: SecretKey

    private val username = "testUser"

    private lateinit var objectUnderTest: JwtService

    companion object {
        private val EXPIRATION_ONE_DAY = TimeUnit.DAYS.toMillis(1)
        private val EXPIRATION_SEVEN_DAYS = TimeUnit.DAYS.toMillis(7)
    }

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

    @Test
    fun `when extract username is triggered then expect jwt authentication exception`() {
        val invalidToken = "invalidToken"

        val actualResult = assertThrows<JwtAuthenticationException> { objectUnderTest.extractUsername(invalidToken) }

        assertEquals("Failed to extract claims from token.", actualResult.message)
    }

    @Test
    fun `when is token valid is triggered then expect true`() {
        val username = "testuser"
        val expirationDate = Date(System.currentTimeMillis() + 3600000) // Set token to expire in 1 hour
        val token = Jwts.builder()
            .claim("sub", username)
            .claim("exp", expirationDate)
            .signWith(secretKey)
            .compact()
        val userDetails = mockk<UserDetails>()
        every { userDetails.username } returns username

        val actualResult = objectUnderTest.isTokenValid(token, userDetails)

        assertTrue(actualResult)
    }

    @Test
    fun `when generate access token is triggered then expect new token`() {
        val userDetails = mockk<UserDetails>()
        every { userDetails.username } returns "testuser"

        val token = objectUnderTest.generateAccessToken(userDetails)
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseClaimsJws(token)
            .body


        assertEquals("testuser", claims.subject)
        assertTrue(claims.expiration.time <= System.currentTimeMillis() + EXPIRATION_ONE_DAY)

    }
}