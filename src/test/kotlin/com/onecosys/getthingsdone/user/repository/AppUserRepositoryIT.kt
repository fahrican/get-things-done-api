package com.onecosys.getthingsdone.user.repository

import com.onecosys.getthingsdone.user.domain.AppUser
import com.onecosys.getthingsdone.user.infrastructure.AppUserRepository
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
    val objectUnderTest: AppUserRepository
) {

    companion object {
        private const val USER_EMAIL = "faris.diab@example.com"
        private const val USERNAME = "faris-diab"
    }


    private val user = AppUser(
        firstName = "Faris",
        lastName = "Diab",
        email = USER_EMAIL,
        appUsername = USERNAME,
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

        val actualUser: AppUser? = objectUnderTest.findByEmail(USER_EMAIL)

        assertNotNull(actualUser)
        assertEquals(USER_EMAIL, actualUser?.email)
    }

    @Test
    fun `when user email does not exist then check if user is null`() {
        val email = "none-existent@example.com"

        val actualUser: AppUser? = objectUnderTest.findByEmail(email)

        assertNull(actualUser)
    }

    @Test
    fun `when new user gets persisted then check if username can be found`() {
        entityManager.flush()

        val actualUser: AppUser? = objectUnderTest.findByAppUsername(USERNAME)

        assertNotNull(actualUser)
        assertEquals(USERNAME, actualUser?.appUsername)
    }

    @Test
    fun `when user username does not exist then check if user is null`() {
        val username = "malek-abul"

        val actualUser: AppUser? = objectUnderTest.findByAppUsername(username)

        assertNull(actualUser)
    }

    @Test
    fun `when find by username and find by email are called then check for the some properties`() {
        entityManager.flush()

        val foundUserByUsername: AppUser? = objectUnderTest.findByAppUsername(USERNAME)
        val foundUserByEmail: AppUser? = objectUnderTest.findByEmail(USER_EMAIL)

        assertEquals(foundUserByEmail?.authorities, foundUserByUsername?.authorities)
        assertEquals(foundUserByEmail?.password, foundUserByUsername?.password)
        assertEquals(foundUserByEmail?.username, foundUserByUsername?.username)
        assertEquals(foundUserByEmail?.isEnabled, foundUserByUsername?.isEnabled)
        assertEquals(true, foundUserByUsername?.isAccountNonExpired)
        assertEquals(true, foundUserByUsername?.isAccountNonLocked)
        assertEquals(true, foundUserByUsername?.isCredentialsNonExpired)
        assertEquals(true, foundUserByEmail?.isAccountNonExpired)
        assertEquals(true, foundUserByEmail?.isAccountNonLocked)
        assertEquals(true, foundUserByEmail?.isCredentialsNonExpired)
    }
}