package com.onecosys.getthingsdone.authorization

import com.onecosys.getthingsdone.authorization.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): User?
}