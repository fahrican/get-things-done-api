package com.onecosys.getthingsdone.user.service

import com.onecosys.getthingsdone.user.entity.User

interface AuthUserService {

    fun getCurrentAuthenticatedUser(): User
}