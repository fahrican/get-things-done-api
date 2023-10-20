package com.onecosys.getthingsdone.config

import com.onecosys.getthingsdone.authentication.error.JwtAuthenticationException
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
    private val userService: UserDetailsService
) : OncePerRequestFilter() {

    companion object {
        private const val AUTH_HEADER = "Authorization"
        private const val BEARER_TOKEN_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader(AUTH_HEADER)

        if (authHeader?.startsWith(BEARER_TOKEN_PREFIX) == true) {
            val jwt = authHeader.drop(BEARER_TOKEN_PREFIX.length)

            if (SecurityContextHolder.getContext().authentication == null) {
                runCatching {
                    val userEmail = jwtService.extractUsername(jwt)
                    val userDetails: UserDetails = userService.loadUserByUsername(userEmail)

                    if (jwtService.isTokenValid(jwt, userDetails)) {
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
