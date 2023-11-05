package com.onecosys.getthingsdone.authorization

import com.onecosys.getthingsdone.authorization.model.BearerToken
import com.onecosys.getthingsdone.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BearerTokenRepository : JpaRepository<BearerToken, Long> {

    fun findByToken(token: String): BearerToken?

    fun findAllByUserOrderByCreatedAtAsc(user: User): List<BearerToken>
}