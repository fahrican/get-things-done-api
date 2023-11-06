package com.onecosys.getthingsdone.authentication.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service

@Service
class LogoutService : LogoutHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val AUTH_HEADER = "Authorization"
        const val BEARER_TOKEN_PREFIX = "Bearer "
    }

    @Transactional
    override fun logout(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        val authHeader: String = request?.getHeader(AUTH_HEADER) ?: return
        if (authHeader.startsWith(BEARER_TOKEN_PREFIX)) {
            val jwt: String = authHeader.substring(BEARER_TOKEN_PREFIX.length)
            // Log the event, perform any other necessary cleanup, etc.
            log.info("User logged out with token: $jwt")
        }
        SecurityContextHolder.clearContext()
    }
}