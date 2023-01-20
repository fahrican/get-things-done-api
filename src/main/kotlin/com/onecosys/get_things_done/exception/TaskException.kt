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
        val error = ApiError(_message = badRequestException.message, status = badRequestException.httpStatus)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(TaskNotFoundException::class)
    fun handleTaskNotFoundException(taskNotFoundException: TaskNotFoundException): ResponseEntity<ApiError> {
        val error = ApiError(_message = taskNotFoundException.message, status = taskNotFoundException.httpStatus)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(exception: Exception): ResponseEntity<ApiError> {
        val error = ApiError(_message = exception.message, status = HttpStatus.INTERNAL_SERVER_ERROR)
        return ResponseEntity(error, error.status)
    }
}

data class TaskNotFoundException(val httpStatus: HttpStatus = HttpStatus.NOT_FOUND, override val message: String) : HttpClientErrorException(httpStatus, message)

data class BadRequestException(val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST, override val message: String) : HttpClientErrorException(httpStatus, message)