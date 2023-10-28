package com.onecosys.getthingsdone.authentication.error

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class AuthenticationExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        exception: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errorDetails = exception.bindingResult.fieldErrors.joinToString("\n") { "${it.field}: ${it.defaultMessage}" }
        return buildResponseEntity(HttpStatus.BAD_REQUEST, errorDetails)
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

    private fun buildResponseEntity(status: HttpStatus, message: String?): ResponseEntity<Any> {
        val error = AuthenticationError(message = message, status = status)
        return ResponseEntity(error, status)
    }
}

class SignUpException(message: String) : RuntimeException(message)

class EmailNotFoundException(message: String) : RuntimeException(message)

class JwtAuthenticationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class UserMismatchException(message: String) : RuntimeException(message)

class UserNotFoundException(message: String) : RuntimeException(message)

class IncorrectPasswordException(message: String) : RuntimeException(message)

class PasswordConfirmationMismatchException(message: String) : RuntimeException(message)

class UsernamePasswordMismatchException(message: String) : RuntimeException(message)
