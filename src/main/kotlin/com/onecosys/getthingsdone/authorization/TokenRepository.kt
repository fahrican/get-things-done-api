package com.onecosys.getthingsdone.authorization

import com.onecosys.getthingsdone.authorization.model.BearerToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : JpaRepository<BearerToken, Long> {

    @Query(
        value = """
            select t from BearerToken t inner join User u 
            on t.user.id = u.id 
            where u.id = :id and (t.expired = false or t.revoked = false)
            """
    )
    fun findAllValidTokenByUser(id: Long?): List<BearerToken?>?

    fun findByToken(token: String): BearerToken?
}