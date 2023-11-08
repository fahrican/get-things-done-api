package com.onecosys.getthingsdone.user.model.dto

import jakarta.validation.constraints.NotBlank

data class UserPasswordUpdateRequest(

    @field:NotBlank
    val currentPassword: String,

    @field:NotBlank
    val newPassword: String,

    @field:NotBlank
    val newPasswordConfirmation: String,
)
