package com.onecosys.getthingsdone.auth.repository

import com.onecosys.getthingsdone.auth.domain.AppUser
import com.onecosys.getthingsdone.auth.domain.VerificationToken
import com.onecosys.getthingsdone.auth.infrastructure.VerificationTokenRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.Instant
import java.time.temporal.ChronoUnit


@DataJpaTest
class VerificationTokenRepositoryIT @Autowired constructor(
    val entityManager: TestEntityManager,
    val objectUnderTest: VerificationTokenRepository
) {

    companion object {
        private const val DUMMY_TOKEN = "test-token"
    }

    val user = AppUser()

    @BeforeEach
    fun setup() {
        entityManager.persist(user)
    }

    @AfterEach
    fun tearDown() {
        entityManager.clear()
    }


    @Test
    fun `when new token gets stored then check if it can be found`() {
        val token = VerificationToken(
            token = DUMMY_TOKEN,
            appUser = user,
            expiryDate = Instant.now().plus(1, ChronoUnit.DAYS)
        )
        entityManager.persist(token)
        entityManager.flush()

        val foundToken = objectUnderTest.findByToken(DUMMY_TOKEN)

        assertNotNull(foundToken)
        assertEquals(DUMMY_TOKEN, foundToken?.token)
    }

    @Test
    fun `when token does not exist then check if the result is null`() {
        val nonExistentToken = "non-existent-token"

        val foundToken = objectUnderTest.findByToken(nonExistentToken)

        assertNull(foundToken)
    }
}