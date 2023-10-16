package com.onecosys.getthingsdone.config

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
        private const val JWT_TOKEN_START_INDEX = 7
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")

        // Extract JWT from header and validate
        authHeader?.takeIf { it.startsWith("Bearer ") }?.let {
            val jwt = it.substring(JWT_TOKEN_START_INDEX)
            val userEmail: String = jwtService.extractUsername(jwt)

            // Set user authentication in the context if the JWT is valid and no authentication exists
            if (SecurityContextHolder.getContext().authentication == null) {
                val userDetails: UserDetails = userService.loadUserByUsername(userEmail)
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    val authToken =
                        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities).apply {
                            details = WebAuthenticationDetailsSource().buildDetails(request)
                        }
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}