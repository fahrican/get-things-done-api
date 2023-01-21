package com.onecosys.get_things_done.error_handling

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ApiError(
        val message: String? = "Something went wrong",
        val status: HttpStatus,
        val code: Int = status.value(),
        val timestamp: LocalDateTime = LocalDateTime.now(),
)