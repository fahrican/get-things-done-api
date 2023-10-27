package com.onecosys.getthingsdone.user.dto

data class UserPasswordUpdateRequest(
    val currentPassword: String,
    val newPassword: String,
    val newPasswordConfirmation: String,
)
