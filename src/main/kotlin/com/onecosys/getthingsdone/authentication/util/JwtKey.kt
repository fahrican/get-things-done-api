package com.onecosys.getthingsdone.authentication.util

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class JwtKey {

    @Value("\${jwt.secret-key}")
    private lateinit var secretKeyString: String

    val secretKey: SecretKey by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString)) }
}