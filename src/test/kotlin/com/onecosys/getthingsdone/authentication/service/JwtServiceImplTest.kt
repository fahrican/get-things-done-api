package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.util.JwtKey
import com.onecosys.getthingsdone.error.JwtAuthenticationException
import io.jsonwebtoken.Jwts
import io.mockk.MockKAnnotations
import io.mockk.every
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

    companion object {
        private const val USERNAME = "abu-ali"
        private val EXPIRATION_ONE_DAY = TimeUnit.DAYS.toMillis(1)
        private val EXPIRATION_SEVEN_DAYS = TimeUnit.DAYS.toMillis(7)
    }

    private val mockJwtKey = mockk<JwtKey>(relaxed = true)

    private lateinit var secretKey: SecretKey

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
            .claim("sub", USERNAME)
            .signWith(secretKey)
            .compact()

        val actualUsername = objectUnderTest.extractUsername(token)

        assertEquals(USERNAME, actualUsername)
    }

    @Test
    fun `when extract username is triggered then expect jwt authentication exception`() {
        val invalidToken = "invalid-token"

        val actualResult = assertThrows<JwtAuthenticationException> { objectUnderTest.extractUsername(invalidToken) }

        assertEquals("Failed to extract claims from token.", actualResult.message)
    }

    @Test
    fun `when is token valid is triggered then expect true`() {
        val expirationDate = Date(System.currentTimeMillis() + 3600000) // Set token to expire in 1 hour
        val token = Jwts.builder()
            .claim("sub", USERNAME)
            .claim("exp", expirationDate)
            .signWith(secretKey)
            .compact()
        val userDetails = mockk<UserDetails>()
        every { userDetails.username } returns USERNAME

        val actualResult = objectUnderTest.isTokenValid(token, userDetails)

        assertTrue(actualResult)
    }

    @Test
    fun `when generate access token is triggered then expect new token`() {
        val userDetails = mockk<UserDetails>()
        every { userDetails.username } returns USERNAME

        val accessToken = objectUnderTest.generateAccessToken(userDetails)
        val actualClaims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(accessToken)
            .payload

        assertEquals(USERNAME, actualClaims.subject)
        assertTrue(actualClaims.expiration.time <= System.currentTimeMillis() + EXPIRATION_ONE_DAY)
    }

    @Test
    fun `when generate refresh token is triggered then expect new token`() {
        val userDetails = mockk<UserDetails>()
        every { userDetails.username } returns USERNAME

        val refreshToken = objectUnderTest.generateRefreshToken(userDetails)
        val actualClaims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(refreshToken)
            .payload

        assertEquals(USERNAME, actualClaims.subject)
        assertTrue(actualClaims.expiration.time <= System.currentTimeMillis() + EXPIRATION_SEVEN_DAYS)
    }
}