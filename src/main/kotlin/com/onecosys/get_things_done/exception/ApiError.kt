package com.onecosys.get_things_done.exception

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

//TODO: tr to include "path":
data class ApiError(
        private val _message: String?,
        val status: HttpStatus,
        val code: Int = status.value(),
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val path: String? = null
){
    val message: String
        get() = _message ?: "Something went wrong"
}