package com.onecosys.getthingsdone.error

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
        UserNotFoundException::class,
        TaskNotFoundException::class
    )
    fun handleNotFoundException(exception: RuntimeException): ResponseEntity<ApiError> =
        buildResponseEntity(HttpStatus.NOT_FOUND, exception.message)

    @ExceptionHandler(
        SignUpException::class,
        PasswordMismatchException::class
    )
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

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(exception: BadRequestException): ResponseEntity<ApiError> =
        buildResponseEntity(HttpStatus.BAD_REQUEST, exception.message)
}

class TaskNotFoundException(message: String) : RuntimeException(message)

class BadRequestException(message: String) : RuntimeException(message)

class SignUpException(message: String) : RuntimeException(message)

class JwtAuthenticationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class UserNotFoundException(message: String) : RuntimeException(message)

class PasswordMismatchException(message: String) : RuntimeException(message)

class UsernamePasswordMismatchException(message: String) : RuntimeException(message)

class AccountVerificationException(message: String) : RuntimeException(message)

class TokenExpiredException(message: String) : RuntimeException(message)

class JwtKeyException(message: String) : IllegalStateException(message)