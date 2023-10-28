package com.onecosys.getthingsdone.task.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class TaskExceptionAdvice : ResponseEntityExceptionHandler() {

    private fun buildResponseEntity(status: HttpStatus, message: String?): ResponseEntity<ApiError> {
        val error = ApiError(message = message, status = status)
        return ResponseEntity(error, status)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(badRequestException: BadRequestException): ResponseEntity<ApiError> {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, badRequestException.message)
    }

    @ExceptionHandler(TaskNotFoundException::class)
    fun handleTaskNotFoundException(taskNotFoundException: TaskNotFoundException): ResponseEntity<ApiError> {
        return buildResponseEntity(HttpStatus.NOT_FOUND, taskNotFoundException.message)
    }
}

data class TaskNotFoundException(override val message: String) : RuntimeException(message)

data class BadRequestException(override val message: String) : RuntimeException(message)
