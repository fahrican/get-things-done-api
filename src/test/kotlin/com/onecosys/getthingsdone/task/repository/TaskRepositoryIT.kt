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

        val actualTask: Task? = taskRepository.findTaskByIdAndUser(task.id, user)

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

        val actualTask: Task? = taskRepository.findTaskByIdAndUser(task.id, anotherUser)

        assertNull(actualTask)
    }

    @Test
    fun `when does description exist is queried then expect true`() {
        entityManager.flush()

        val actualResult: Boolean = taskRepository.doesDescriptionExist(expectedDescription)

        assertEquals(true, actualResult)
    }

    @Test
    fun `when does description exist is queried then expect false`() {
        val expectedDescription = "Unique Description"

        val actualResult: Boolean = taskRepository.doesDescriptionExist(expectedDescription)

        assertEquals(false, actualResult)
    }

    @Test
    fun `when find all by user and is task open is false order by id asc is queried then expect one task`() {
        val expectedDescription = "Task 2"
        val task2 = Task().apply { description = expectedDescription; isTaskOpen = false; appUser = user }
        entityManager.persist(task2)
        entityManager.flush()

        val tasks: Set<Task> = taskRepository.findAllByUserAndIsTaskOpenOrderByIdAsc(user, false)

        assertEquals(1, tasks.size)
        assertEquals(expectedDescription, tasks.first().description)
    }

    @Test
    fun `when find all by user and is task open is true order by id asc is queried then expect one task`() {
        entityManager.flush()

        val tasks: Set<Task> = taskRepository.findAllByUserAndIsTaskOpenOrderByIdAsc(user, true)

        assertEquals(1, tasks.size)
        assertEquals(expectedDescription, tasks.first().description)
    }

    @Test
    fun `when find all by user order by id asc is queried then expect three tasks`() {
        val task1 = Task().apply { description = "Task 1"; appUser = user }
        val task2 = Task().apply { description = "Task 2"; appUser = user }
        entityManager.persist(task1)
        entityManager.persist(task2)
        entityManager.flush()

        val tasks: Set<Task> = taskRepository.findAllByUserOrderByIdAsc(user)

        assertEquals(3, tasks.size)
    }

    @Test
    fun `when find all by user order by id asc is queried then expect zero tasks`() {
        entityManager.clear()

        val tasks: Set<Task> = taskRepository.findAllByUserOrderByIdAsc(user)

        assertEquals(0, tasks.size)
    }
}