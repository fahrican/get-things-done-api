package com.onecosys.getthingsdone.user.repository

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

@DataJpaTest
class AppUserRepositoryIT @Autowired constructor(
    val entityManager: TestEntityManager,
    val appUserRepository: AppUserRepository
) {

    val userEmail = "faris.diab@example.com"
    val username = "faris-diab"
    val user = AppUser(
        firstName = "Faris",
        lastName = "Diab",
        email = userEmail,
        appUsername = username,
        appPassword = "SecurePassword123",
        isVerified = true
    )

    @BeforeEach
    fun setup() {
        entityManager.persist(user)
    }

    @AfterEach
    fun tearDown() {
        entityManager.clear()
    }


    @Test
    fun `when new user gets persisted then check if email can be found`() {
        entityManager.flush()

        val actualUser: AppUser? = appUserRepository.findByEmail(userEmail)

        assertNotNull(actualUser)
        assertEquals(userEmail, actualUser?.email)
    }

    @Test
    fun `when user email does not exist then check if user is null`() {
        val email = "none-existent@example.com"

        val actualUser: AppUser? = appUserRepository.findByEmail(email)

        assertNull(actualUser)
    }

    @Test
    fun `when new user gets persisted then check if username can be found`() {
        entityManager.flush()

        val foundUser = appUserRepository.findByAppUsername(username)

        assertNotNull(foundUser)
        assertEquals(username, foundUser?.appUsername)
    }

    @Test
    fun `when user username does not exist then check if user is null`() {
        val username = "malek-abul"

        val foundUser = appUserRepository.findByAppUsername(username)

        assertNull(foundUser)
    }
}