package com.onecosys.getthingsdone.authentication.error

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class AuthError(
    val message: String? = "Something went wrong",
    val status: HttpStatus,
    val code: Int = status.value(),
    val timestamp: LocalDateTime = LocalDateTime.now()
)
