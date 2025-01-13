package com.onecosys.getthingsdone.auth.infrastructure

import com.onecosys.getthingsdone.auth.domain.VerificationToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface VerificationTokenRepository : JpaRepository<VerificationToken, Long> {

    @Query("select v from VerificationToken v where v.token = ?1")
    fun findByToken(token: String): VerificationToken?
}
