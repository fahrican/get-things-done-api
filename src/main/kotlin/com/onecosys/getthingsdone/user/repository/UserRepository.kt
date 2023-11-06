package com.onecosys.getthingsdone.user.repository

import com.onecosys.getthingsdone.user.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): User?

    fun findBy_username(username: String): User?
}