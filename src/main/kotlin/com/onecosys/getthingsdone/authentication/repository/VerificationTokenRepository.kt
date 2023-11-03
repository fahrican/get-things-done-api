package com.onecosys.getthingsdone.authentication.repository

import com.onecosys.getthingsdone.authentication.dto.VerificationToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VerificationTokenRepository : JpaRepository<VerificationToken, Long> {

    fun findByToken(token: String): VerificationToken?
}
