package com.onecosys.getthingsdone.auth.domain

class AccountVerificationException(message: String) : RuntimeException(message)

class BadUserRequestException(message: String) : RuntimeException(message)

class JwtAuthenticationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class JwtKeyException(message: String) : IllegalStateException(message)

class PasswordMismatchException(message: String) : RuntimeException(message)

class SignUpException(message: String) : RuntimeException(message)

class TokenExpiredException(message: String) : RuntimeException(message)

class UsernamePasswordMismatchException(message: String) : RuntimeException(message)

class UserNotFoundException(message: String) : RuntimeException(message)
