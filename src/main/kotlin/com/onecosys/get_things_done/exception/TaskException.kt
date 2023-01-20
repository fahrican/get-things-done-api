package com.onecosys.get_things_done.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class TaskException {

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(badRequestException: BadRequestException): ResponseEntity<ApiError> {
        val error = ApiError(_message = badRequestException.message, status = HttpStatus.BAD_REQUEST)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(TaskNotFoundException::class)
    fun handleTaskNotFoundException(taskNotFoundException: TaskNotFoundException): ResponseEntity<ApiError> {
        val error = ApiError(_message = taskNotFoundException.message, status = HttpStatus.NOT_FOUND)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(exception: Exception): ResponseEntity<ApiError> {
        val error = ApiError(_message = exception.message, status = HttpStatus.INTERNAL_SERVER_ERROR)
        return ResponseEntity(error, error.status)
    }
}

//TODO: Try to use HttpClientErrorException() or
data class TaskNotFoundException(override val message: String): RuntimeException()

data class BadRequestException(override val message: String): RuntimeException()