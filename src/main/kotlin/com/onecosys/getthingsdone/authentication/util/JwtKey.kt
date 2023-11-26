package com.onecosys.getthingsdone.authentication.util

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import javax.crypto.SecretKey


@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtConfig {
    lateinit var secretKey: String
}

@Component
class JwtKey(private val jwtConfig: JwtConfig) {

    private val log = LoggerFactory.getLogger(JwtKey::class.java)

    val secretKey: SecretKey by lazy {
        try {
            Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.secretKey))
        } catch (ise: IllegalStateException) {
            log.error("Error decoding JWT secret key: ${ise.message}")
            throw IllegalStateException("Invalid JWT secret key")
        }
    }
}