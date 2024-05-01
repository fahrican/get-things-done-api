package com.onecosys.getthingsdone.task.repository

import com.onecosys.getthingsdone.task.entity.Task
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
    val expectedDescription = "Test Task"

    val task = Task().apply { description = expectedDescription; appUser = user }

    @BeforeEach
    fun setup() {
        entityManager.persist(user)
        entityManager.persist(task)
    }

    @AfterEach
    fun tearDown() {
        entityManager.clear()
    }

    @Test
    fun `when find task by id and user is queried then expect pre-defined description`() {
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
        entityManager.persist(anotherUser)
        entityManager.flush()

        val actualTask = taskRepository.findTaskByIdAndUser(task.id, anotherUser)

        assertNull(actualTask)
    }

    @Test
    fun `when does description exist is queried then expect true`() {
        entityManager.flush()

        val actualResult = taskRepository.doesDescriptionExist(expectedDescription)

        assertEquals(true, actualResult)
    }

    @Test
    fun `when does description exist is queried then expect false`() {
        val expectedDescription = "Unique Description"

        val actualResult = taskRepository.doesDescriptionExist(expectedDescription)

        assertEquals(false, actualResult)
    }

    @Test
    fun `test findAllByUserAndIsTaskOpenOrderByIdAsc`() {
        entityManager.persist(user)
        val task1 = Task().apply { description = "Task 1"; isTaskOpen = true; appUser = user }
        val task2 = Task().apply { description = "Task 2"; isTaskOpen = false; appUser = user }
        entityManager.persist(task1)
        entityManager.persist(task2)
        entityManager.flush()

        val tasks = taskRepository.findAllByUserAndIsTaskOpenOrderByIdAsc(user, true)

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