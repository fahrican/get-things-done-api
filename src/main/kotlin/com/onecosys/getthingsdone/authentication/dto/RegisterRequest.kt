package com.onecosys.getthingsdone.authentication.dto

import com.onecosys.getthingsdone.authorization.model.Role

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val role: Role,
)
