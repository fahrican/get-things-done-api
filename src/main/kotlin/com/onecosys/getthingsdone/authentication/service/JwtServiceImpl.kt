package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.util.JwtKey
import com.onecosys.getthingsdone.error.JwtAuthenticationException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import java.util.concurrent.TimeUnit

@Service
class JwtServiceImpl(private val jwtKey: JwtKey) : JwtService {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private val EXPIRATION_ONE_DAY = TimeUnit.DAYS.toMillis(1)
        private val EXPIRATION_SEVEN_DAYS = TimeUnit.DAYS.toMillis(7)
    }

    override fun extractUsername(token: String): String = extractAllClaims(token).subject

    override fun isTokenValid(token: String, userDetails: UserDetails): Boolean =
        extractUsername(token) == userDetails.username && !isTokenExpired(token)

    override fun generateAccessToken(userDetails: UserDetails): String =
        generateToken(emptyMap(), userDetails, EXPIRATION_ONE_DAY)

    override fun generateRefreshToken(userDetails: UserDetails): String =
        generateToken(emptyMap(), userDetails, EXPIRATION_SEVEN_DAYS)

    private fun extractAllClaims(token: String): Claims =
        try {
            Jwts.parser()
                .verifyWith(jwtKey.secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            log.error("Failed to extract claims from token: ${e.message}")
            throw JwtAuthenticationException("Failed to extract claims from token.", e)
        }

    private fun isTokenExpired(token: String): Boolean = extractAllClaims(token).expiration.before(Date())

    private fun generateToken(claims: Map<String, Any>, userDetails: UserDetails, expirationTime: Long): String =
        Jwts.builder()
            .claims(claims)
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(jwtKey.secretKey)
            .compact()
}
