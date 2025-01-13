package com.onecosys.getthingsdone.api

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ApiError(
    val message: String? = "Something API related went wrong",
    val status: HttpStatus,
    val code: Int = status.value(),
    val timestamp: LocalDateTime = LocalDateTime.now()
)
