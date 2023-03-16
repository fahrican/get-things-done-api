package com.onecosys.getthingsdone.errorhandling

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class TaskExceptionAdvice {

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(badRequestException: BadRequestException): ResponseEntity<ApiError> {
        val error = ApiError(message = badRequestException.message, status = HttpStatus.BAD_REQUEST)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(TaskNotFoundException::class)
    fun handleTaskNotFoundException(taskNotFoundException: TaskNotFoundException): ResponseEntity<ApiError> {
        val error = ApiError(message = taskNotFoundException.message, status = HttpStatus.NOT_FOUND)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(exception: Exception): ResponseEntity<ApiError> {
        val error = ApiError(message = exception.message, status = HttpStatus.INTERNAL_SERVER_ERROR)
        println("An exception occurred: ${exception.message}")
        exception.printStackTrace()
        return ResponseEntity(error, error.status)
    }
}

data class TaskNotFoundException(override val message: String) : RuntimeException(message)

data class BadRequestException(override val message: String) : RuntimeException(message)
