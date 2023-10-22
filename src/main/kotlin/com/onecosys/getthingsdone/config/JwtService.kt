package com.onecosys.getthingsdone.config

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
class JwtService {

    @Value("\${jwt.secret-key}")
    private lateinit var secretKeyString: String

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString))
    }

    private fun extractAllClaims(token: String): Claims = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .payload

    fun extractUsername(token: String): String = extractAllClaims(token).subject

    private fun isTokenExpired(token: String): Boolean = extractAllClaims(token).expiration.before(Date())

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        return (extractUsername(token) == userDetails.username) && !isTokenExpired(token)
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims = HashMap<String, Any>()
        return generateToken(claims, userDetails)
    }

    private fun setExpirationAfterADay() = Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)

    private fun generateToken(extractClaims: Map<String, Any>, userDetails: UserDetails): String = Jwts.builder()
        .claims(extractClaims)
        .subject(userDetails.username)
        .issuedAt(Date(System.currentTimeMillis()))
        .expiration(setExpirationAfterADay())
        .signWith(secretKey)
        .compact()
}
