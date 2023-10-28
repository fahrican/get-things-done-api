package com.onecosys.getthingsdone.user.error

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class UserError(
    val message: String? = "Something user related went wrong",
    val status: HttpStatus,
    val code: Int = status.value(),
    val timestamp: LocalDateTime = LocalDateTime.now()
)
