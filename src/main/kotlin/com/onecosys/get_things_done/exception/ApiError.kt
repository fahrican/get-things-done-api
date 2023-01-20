package com.onecosys.get_things_done.exception

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ApiError(
        val message: String? = "Something went wrong",
        val status: HttpStatus,
        val code: Int = status.value(),
        val timestamp: LocalDateTime = LocalDateTime.now(),
)