package com.onecosys.get_things_done.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpClientErrorException

@ControllerAdvice
class TaskException {

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(badRequestException: BadRequestException): ResponseEntity<ApiError> {
        val error = ApiError(message = badRequestException.message, status = badRequestException.statusCode)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(TaskNotFoundException::class)
    fun handleTaskNotFoundException(taskNotFoundException: TaskNotFoundException): ResponseEntity<ApiError> {
        val error = ApiError(message = taskNotFoundException.message, status = taskNotFoundException.statusCode)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(exception: Exception): ResponseEntity<ApiError> {
        val error = ApiError(message = exception.message, status = HttpStatus.INTERNAL_SERVER_ERROR)
        return ResponseEntity(error, error.status)
    }
}

data class TaskNotFoundException(override val message: String) : HttpClientErrorException(HttpStatus.NOT_FOUND, message)

data class BadRequestException(override val message: String) : HttpClientErrorException(HttpStatus.BAD_REQUEST, message)