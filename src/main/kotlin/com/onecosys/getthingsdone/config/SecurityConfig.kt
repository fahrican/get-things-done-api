package com.onecosys.getthingsdone.config

import com.onecosys.getthingsdone.authentication.web.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutHandler

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthenticationFilter,
    private val authProvider: AuthenticationProvider,
    private val logoutHandler: LogoutHandler
) {

    @Bean
    fun filterRequests(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .csrf { csrf -> csrf.disable() }  // using lambda to disable CSRF
            .authorizeHttpRequests {
                it.requestMatchers(
                    "api/v1/auth/**",
                    "api/swagger-ui/**",
                    "api/v3/api-docs",
                    "api/swagger-ui.html",
                    "api/v3/api-docs/swagger-config",
                    "api/v3/api-docs"
                ).permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .logout {
                it.logoutUrl("/api/v1/auth/sign-out")
                    .addLogoutHandler(logoutHandler)
                    .logoutSuccessHandler { _, _, _ -> SecurityContextHolder.clearContext() }
            }

        return httpSecurity.build()
    }
}