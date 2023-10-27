package com.onecosys.getthingsdone.authentication.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtServiceImpl : JwtService {

    @Value("\${jwt.secret-key}")
    private lateinit var secretKeyString: String

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString))
    }

    companion object {
        private const val EXPIRATION_AFTER_ONE_DAY: Long = 1000 * 60 * 60 * 24
        private const val EXPIRATION_AFTER_SEVEN_DAYS: Long = 1000 * 60 * 60 * 24 * 7
    }

    override fun extractUsername(token: String): String = extractAllClaims(token).subject

    override fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        return (extractUsername(token) == userDetails.username) && !isTokenExpired(token)
    }

    override fun generateAccessToken(userDetails: UserDetails): String =
        generateToken(HashMap(), userDetails, EXPIRATION_AFTER_ONE_DAY)


    override fun generateRefreshToken(userDetails: UserDetails): String {
        val claims = HashMap<String, Any>()
        return generateToken(claims, userDetails, EXPIRATION_AFTER_SEVEN_DAYS)
    }

    private fun extractAllClaims(token: String): Claims = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .payload


    private fun isTokenExpired(token: String): Boolean = extractAllClaims(token).expiration.before(Date())

    private fun generateToken(
        extractClaims: Map<String, Any>,
        userDetails: UserDetails,
        expirationTime: Long
    ): String = Jwts.builder()
        .claims(extractClaims)
        .subject(userDetails.username)
        .issuedAt(Date(System.currentTimeMillis()))
        .expiration(Date(System.currentTimeMillis() + expirationTime))
        .signWith(secretKey)
        .compact()
}
