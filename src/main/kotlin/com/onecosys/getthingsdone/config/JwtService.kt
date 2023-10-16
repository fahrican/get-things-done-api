package com.onecosys.getthingsdone.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function
import javax.crypto.SecretKey
import kotlin.collections.HashMap

@Service
class JwtService {

    companion object {
        private const val SECRET_KEY = "5ad775079ac450ed902e6707c1cf39efd288bdb783d7d63f9df5bb62b957e4db"
    }

    private fun getSignInKey(): SecretKey? = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY))

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun <T> extractClaim(token: String?, claimsResolver: Function<Claims?, T>): T {
        token?.let {
            val claims = extractAllClaims(token)
            return claimsResolver.apply(claims)
        } ?: run { return claimsResolver.apply(null) }
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(token) { claims: Claims? -> claims?.expiration ?: Date() }
    }

    fun extractUsername(token: String): String {
        return extractClaim(token) { claims: Claims? -> claims?.subject ?: "error getting claims" }
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username: String = extractUsername(token)
        return (username == userDetails.username) && !isTokenExpired(token)
    }

    private fun setExpirationAfterADay() = Date(System.currentTimeMillis() + 1000 * 60 * 24)

    fun generateToken(extractClaims: Map<String, Any>, userDetails: UserDetails): String {
        return Jwts.builder()
            .claims(extractClaims)
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(setExpirationAfterADay())
            .signWith(getSignInKey())
            .compact()
    }

    fun generateToken(userDetails: UserDetails): String {
        return generateToken(HashMap(), userDetails)
    }
}