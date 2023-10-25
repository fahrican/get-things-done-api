package com.onecosys.getthingsdone.authentication.web

import com.onecosys.getthingsdone.authentication.error.JwtAuthenticationException
import com.onecosys.getthingsdone.authentication.service.JwtService
import com.onecosys.getthingsdone.authentication.service.LogoutService.Companion.AUTH_HEADER
import com.onecosys.getthingsdone.authentication.service.LogoutService.Companion.BEARER_TOKEN_PREFIX
import com.onecosys.getthingsdone.authorization.TokenRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userService: UserDetailsService,
    private val tokenRepository: TokenRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader(AUTH_HEADER)

        if (authHeader?.startsWith(BEARER_TOKEN_PREFIX) == true) {
            val jwt: String = authHeader.drop(BEARER_TOKEN_PREFIX.length)

            if (SecurityContextHolder.getContext().authentication == null) {
                runCatching {
                    val userEmail = jwtService.extractUsername(jwt)
                    val userDetails: UserDetails = userService.loadUserByUsername(userEmail)

                    val token = tokenRepository.findByToken(jwt)
                    var isTokenValid = false
                    token?.let {
                        isTokenValid = (!it.expired && !it.revoked)
                    }

                    if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                        val authToken = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        ).apply {
                            details = WebAuthenticationDetailsSource().buildDetails(request)
                        }
                        SecurityContextHolder.getContext().authentication = authToken
                    }
                }.onFailure { throw JwtAuthenticationException("Error processing JWT token", it) }
            }
        }

        filterChain.doFilter(request, response)
    }
}
