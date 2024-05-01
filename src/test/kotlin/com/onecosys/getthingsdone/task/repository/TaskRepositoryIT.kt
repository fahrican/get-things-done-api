package com.onecosys.getthingsdone.task.repository

import com.onecosys.getthingsdone.task.entity.Task
import com.onecosys.getthingsdone.user.entity.AppUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class TaskRepositoryIT @Autowired constructor(
    val entityManager: TestEntityManager,
    val taskRepository: TaskRepository
) {

    val user = AppUser(
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        appUsername = "johndoe",
        appPassword = "securepassword"
    )

    @Test
    fun `when find task by id and user is queried then expect pre-defined description`() {
        val user = AppUser(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            appUsername = "johndoe",
            appPassword = "securepassword"
        )
        entityManager.persist(user)
        val expectedDescription = "Test Task"
        val task = Task().apply { description = expectedDescription; appUser = user }
        entityManager.persist(task)
        entityManager.flush()

        val actualTask = taskRepository.findTaskByIdAndUser(task.id, user)

        assertNotNull(actualTask)
        assertEquals(expectedDescription, actualTask?.description)
    }

    @Test
    fun `when find task by id and user is queried with wrong user then expect null`() {
        val anotherUser = AppUser(
            firstName = "Jane",
            lastName = "Doe",
            email = "jane.doe@example.com",
            appUsername = "janedoe",
            appPassword = "securepassword"
        )
        entityManager.persist(user)
        entityManager.persist(anotherUser)
        val expectedDescription = "Test Task"
        val task = Task().apply { description = expectedDescription;appUser = user }
        entityManager.persist(task)
        entityManager.flush()

        val actualTask = taskRepository.findTaskByIdAndUser(task.id, anotherUser)

        assertNull(actualTask, "Task should not be found for the wrong user")
    }

    @Test
    fun `test existsByDescription`() {
        // Arrange
        val task = Task().apply { description = "Unique Description" }
        entityManager.persist(task)
        entityManager.flush()

        // Act
        val exists = taskRepository.doesDescriptionExist("Unique Description")

        // Assert
        assert(exists)
        assertEquals(true, exists)
    }

    @Test
    fun `test findAllByUserAndIsTaskOpenOrderByIdAsc`() {
        // Arrange
        val user = AppUser(
            firstName = "Alice",
            lastName = "Smith",
            email = "alice.smith@example.com",
            appUsername = "alicesmith",
            appPassword = "securepassword"
        )
        entityManager.persist(user)
        val task1 = Task().apply { description = "Task 1"; isTaskOpen = true; appUser = user }
        val task2 = Task().apply { description = "Task 2"; isTaskOpen = false; appUser = user }
        entityManager.persist(task1)
        entityManager.persist(task2)
        entityManager.flush()

        // Act
        val tasks = taskRepository.findAllByUserAndIsTaskOpenOrderByIdAsc(user, true)

        // Assert
        assert(tasks.size == 1)
        assert(tasks.first().description == "Task 1")
    }

    @Test
    fun `test findAllByUserOrderByIdAsc`() {
        // Arrange
        val user = AppUser(
            firstName = "Bob",
            lastName = "Brown",
            email = "bob.brown@example.com",
            appUsername = "bobbrown",
            appPassword = "securepassword"
        )
        entityManager.persist(user)
        val task1 = Task().apply { description = "Task 1"; appUser = user }
        val task2 = Task().apply { description = "Task 2"; appUser = user }
        entityManager.persist(task1)
        entityManager.persist(task2)
        entityManager.flush()

        // Act
        val tasks = taskRepository.findAllByUserOrderByIdAsc(user)

        // Assert
        assert(tasks.size == 2)
    }
}