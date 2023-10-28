package com.onecosys.getthingsdone.authentication.dto

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val password: String,
    val passwordConfirmation: String,
)
