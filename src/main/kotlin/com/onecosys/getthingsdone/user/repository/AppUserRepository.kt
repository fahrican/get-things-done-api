package com.onecosys.getthingsdone.user.repository

import com.onecosys.getthingsdone.user.entity.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AppUserRepository : JpaRepository<AppUser, Long> {

    fun findByEmail(email: String): AppUser?

    fun findByAppUsername(username: String): AppUser?
}