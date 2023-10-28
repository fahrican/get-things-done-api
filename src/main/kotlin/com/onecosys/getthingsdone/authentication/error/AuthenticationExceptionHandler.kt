package com.onecosys.getthingsdone.authentication.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class AuthenticationExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(exception: UsernameNotFoundException): ResponseEntity<AuthenticationError> {
        val error = AuthenticationError(message = exception.message, status = HttpStatus.NOT_FOUND)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(EmailNotFoundException::class)
    fun handleEmailNotFoundException(exception: EmailNotFoundException): ResponseEntity<AuthenticationError> {
        val error = AuthenticationError(message = exception.message, status = HttpStatus.NOT_FOUND)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(SignUpException::class)
    fun handleSignUpException(exception: SignUpException): ResponseEntity<AuthenticationError> {
        val error = AuthenticationError(message = exception.message, status = HttpStatus.CONFLICT)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(JwtAuthenticationException::class)
    fun handleJwtAuthenticationException(exception: JwtAuthenticationException): ResponseEntity<AuthenticationError> {
        val error = AuthenticationError(message = exception.message, status = HttpStatus.UNAUTHORIZED)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(UserMismatchException::class)
    fun handleUserMismatchException(exception: UserMismatchException): ResponseEntity<AuthenticationError> {
        val error = AuthenticationError(message = exception.message, status = HttpStatus.CONFLICT)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(exception: UserNotFoundException): ResponseEntity<AuthenticationError> {
        val error = AuthenticationError(message = exception.message, status = HttpStatus.NOT_FOUND)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(IncorrectPasswordException::class)
    fun handleIncorrectPasswordException(exception: IncorrectPasswordException): ResponseEntity<AuthenticationError> {
        val error = AuthenticationError(message = exception.message, status = HttpStatus.CONFLICT)
        return ResponseEntity(error, error.status)
    }

    @ExceptionHandler(PasswordConfirmationMismatchException::class)
    fun handlePasswordConfirmationMismatchException(exception: PasswordConfirmationMismatchException): ResponseEntity<AuthenticationError> {
        val error = AuthenticationError(message = exception.message, status = HttpStatus.CONFLICT)
        return ResponseEntity(error, error.status)
    }
}

data class SignUpException(override val message: String) : RuntimeException()

data class EmailNotFoundException(override val message: String) : RuntimeException()

data class JwtAuthenticationException(
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)

data class UserMismatchException(override val message: String) : RuntimeException()

data class UserNotFoundException(override val message: String) : RuntimeException()

data class IncorrectPasswordException(override val message: String) : RuntimeException()

data class PasswordConfirmationMismatchException(override val message: String) : RuntimeException()