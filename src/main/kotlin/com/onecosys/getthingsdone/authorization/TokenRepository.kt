package com.onecosys.getthingsdone.authorization

import com.onecosys.getthingsdone.authorization.model.Token
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : JpaRepository<Token, Long> {


    @Query(
        value = """
            select t from Token t inner join User u 
            on t.user.id = u.id 
            where u.id = :id and (t.expired = false or t.revoked = false)
            """
    )
    fun findAllValidTokenByUser(id: Long?): List<Token?>?


    fun findByToken(token: String): Token?
}