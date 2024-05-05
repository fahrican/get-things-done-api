package com.onecosys.getthingsdone.authentication.repository

import com.onecosys.getthingsdone.authentication.entity.VerificationToken
import com.onecosys.getthingsdone.user.entity.AppUser
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
            token = "test-token",
            appUser = user,
            expiryDate = Instant.now().plus(1, ChronoUnit.DAYS)
        )
        entityManager.persist(token)
        entityManager.flush()

        val foundToken = objectUnderTest.findByToken("test-token")

        assertNotNull(foundToken)
        assertEquals("test-token", foundToken?.token)
    }

    @Test
    fun `when token does not exist then check if the result is null`() {
        val nonExistentToken = "non-existent-token"

        val foundToken = objectUnderTest.findByToken(nonExistentToken)

        assertNull(foundToken)
    }
}