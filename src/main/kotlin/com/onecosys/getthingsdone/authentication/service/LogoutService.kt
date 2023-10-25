package com.onecosys.getthingsdone.authentication.service

import com.onecosys.getthingsdone.authorization.TokenRepository
import com.onecosys.getthingsdone.authorization.model.Token
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service

@Service
class LogoutService(private val repository: TokenRepository) : LogoutHandler {

    companion object {
        const val AUTH_HEADER = "Authorization"
        const val BEARER_TOKEN_PREFIX = "Bearer "
    }

    override fun logout(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        val authHeader: String? = request?.getHeader(AUTH_HEADER)
        if (authHeader?.startsWith(BEARER_TOKEN_PREFIX) == true) {
            val jwt: String = authHeader.drop(BEARER_TOKEN_PREFIX.length)
            val storedToken: Token? = repository.findByToken(jwt)
            storedToken?.apply {
                this.expired = true
                this.revoked = true
                repository.save(storedToken)
            }
        }
    }
}