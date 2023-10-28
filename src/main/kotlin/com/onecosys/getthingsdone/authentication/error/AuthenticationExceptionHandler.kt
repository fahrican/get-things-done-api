package com.onecosys.getthingsdone.authentication.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class AuthenticationExceptionHandler {

    private fun buildResponseEntity(status: HttpStatus, message: String?): ResponseEntity<Any> {
        val error = AuthenticationError(message = message, status = status)
        return ResponseEntity(error, status)
    }

    @ExceptionHandler(UsernameNotFoundException::class, EmailNotFoundException::class, UserNotFoundException::class)
    fun handleNotFoundException(exception: RuntimeException): ResponseEntity<Any> =
        buildResponseEntity(HttpStatus.NOT_FOUND, exception.message)

    @ExceptionHandler(
        SignUpException::class,
        UserMismatchException::class,
        IncorrectPasswordException::class,
        PasswordConfirmationMismatchException::class
    )
    fun handleConflictException(exception: RuntimeException): ResponseEntity<Any> =
        buildResponseEntity(HttpStatus.CONFLICT, exception.message)

    @ExceptionHandler(JwtAuthenticationException::class, UsernamePasswordMismatchException::class)
    fun handleUnauthorizedException(exception: RuntimeException): ResponseEntity<Any> =
        buildResponseEntity(HttpStatus.UNAUTHORIZED, exception.message)
}

class SignUpException(message: String) : RuntimeException(message)

class EmailNotFoundException(message: String) : RuntimeException(message)

class JwtAuthenticationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class UserMismatchException(message: String) : RuntimeException(message)

class UserNotFoundException(message: String) : RuntimeException(message)

class IncorrectPasswordException(message: String) : RuntimeException(message)

class PasswordConfirmationMismatchException(message: String) : RuntimeException(message)

class UsernamePasswordMismatchException(message: String) : RuntimeException(message)
