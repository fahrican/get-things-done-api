package com.onecosys.getthingsdone.user.model.dto

data class UserInfoResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
)
