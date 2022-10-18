package com.onecosys.get_things_done.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
data class TaskNotFoundException(override val message: String): RuntimeException()