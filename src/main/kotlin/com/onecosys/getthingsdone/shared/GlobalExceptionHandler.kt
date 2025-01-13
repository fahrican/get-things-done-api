package com.onecosys.getthingsdone.shared

import com.onecosys.getthingsdone.security.domain.AccountVerificationException
import com.onecosys.getthingsdone.security.domain.JwtAuthenticationException
import com.onecosys.getthingsdone.security.domain.SignUpException
import com.onecosys.getthingsdone.security.domain.TokenExpiredException
import com.onecosys.getthingsdone.security.domain.UsernamePasswordMismatchException
import com.onecosys.getthingsdone.task.domain.BadTaskRequestException
import com.onecosys.getthingsdone.task.domain.TaskNotFoundException
import com.onecosys.getthingsdone.user.domain.BadUserRequestException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private fun buildResponseEntity(status: HttpStatus, message: String?): ResponseEntity<ApiError> {
        val error = ApiError(message = message, status = status)
        return ResponseEntity(error, status)
    }

    @ExceptionHandler(
        UsernameNotFoundException::class,
        TaskNotFoundException::class
    )
    fun handleNotFoundException(exception: RuntimeException): ResponseEntity<ApiError> =
        buildResponseEntity(HttpStatus.NOT_FOUND, exception.message)

    @ExceptionHandler(SignUpException::class)
    fun handleConflictException(exception: RuntimeException): ResponseEntity<ApiError> =
        buildResponseEntity(HttpStatus.CONFLICT, exception.message)

    @ExceptionHandler(
        JwtAuthenticationException::class,
        UsernamePasswordMismatchException::class,
        AccountVerificationException::class,
        TokenExpiredException::class
    )
    fun handleUnauthorizedException(exception: RuntimeException): ResponseEntity<ApiError> =
        buildResponseEntity(HttpStatus.UNAUTHORIZED, exception.message)

    @ExceptionHandler(BadUserRequestException::class, BadTaskRequestException::class)
    fun handleBadRequestException(exception: RuntimeException): ResponseEntity<ApiError> =
        buildResponseEntity(HttpStatus.BAD_REQUEST, exception.message)
}
