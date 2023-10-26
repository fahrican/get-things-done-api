package com.onecosys.getthingsdone.user

data class PasswordChangeRequest(
    val currentPassword: String,
    val newPassword: String,
    val newPasswordConfirmation: String,
)
