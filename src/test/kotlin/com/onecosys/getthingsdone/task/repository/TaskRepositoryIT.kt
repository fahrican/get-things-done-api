package com.onecosys.getthingsdone.task.repository

import com.onecosys.getthingsdone.task.entity.Task
import com.onecosys.getthingsdone.user.entity.AppUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class TaskRepositoryIT @Autowired constructor(
    val entityManager: TestEntityManager,
    val taskRepository: TaskRepository
) {

    @Test
    fun `test findTaskByIdAndUser`() {
        // Arrange
        val user = AppUser(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            appUsername = "johndoe",
            appPassword = "securepassword"
        )
        entityManager.persist(user)
        val task = Task().apply { description = "Test Task"; appUser = user }
        entityManager.persist(task)
        entityManager.flush()

        // Act
        val foundTask = taskRepository.findTaskByIdAndUser(task.id, user)

        // Assert
        assert(foundTask != null)
        assert(foundTask?.description == "Test Task")
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