package com.onecosys.getthingsdone.user.model.dto

data class UserPasswordUpdateRequest(
    val currentPassword: String,
    val newPassword: String,
    val newPasswordConfirmation: String,
)
