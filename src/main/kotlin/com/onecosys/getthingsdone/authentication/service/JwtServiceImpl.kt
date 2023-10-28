package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authentication.error.JwtAuthenticationException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey

@Service
class JwtServiceImpl : JwtService {

    @Value("\${jwt.secret-key}")
    private lateinit var secretKeyString: String

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString))
    }

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
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            throw JwtAuthenticationException("Failed to extract claims from token.", e)
        }

    private fun isTokenExpired(token: String): Boolean = extractAllClaims(token).expiration.before(Date())

    private fun generateToken(claims: Map<String, Any>, userDetails: UserDetails, expirationTime: Long): String =
        Jwts.builder()
            .claims(claims)
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(secretKey)
            .compact()
}
