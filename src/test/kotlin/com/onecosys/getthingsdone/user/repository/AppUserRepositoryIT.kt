package com.onecosys.getthingsdone.user.repository

import com.onecosys.getthingsdone.user.entity.AppUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class AppUserRepositoryIT @Autowired constructor(
    val entityManager: TestEntityManager,
    val appUserRepository: AppUserRepository
) {

    @Test
    fun `when new user gets persisted then check if email can be found`() {
        val userEmail = "faris.diab@example.com"
        val user = AppUser(
            firstName = "Faris",
            lastName = "Diab",
            email = userEmail,
            appUsername = "faris-diab",
            appPassword = "SecurePassword123",
            isVerified = true
        )
        entityManager.persist(user)
        entityManager.flush()

        val foundUser = appUserRepository.findByEmail(userEmail)

        assertNotNull(foundUser)
        assertEquals(userEmail, foundUser?.email)
    }

    @Test
    fun `when user email does not exist then check if user is null`() {
        val email = "none-existent@example.com"

        val foundUser = appUserRepository.findByEmail(email)

        assertNull(foundUser)
    }

    @Test
    fun `when new user gets persisted then check if username can be found`() {
        val username = "nancy-ajram"
        val user = AppUser(
            firstName = "Nancy",
            lastName = "Ajram",
            email = "nancy.ajram@example.com",
            appUsername = username,
            appPassword = "SecurePassword456",
            isVerified = true
        )
        entityManager.persist(user)
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