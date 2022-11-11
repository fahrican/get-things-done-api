package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.entity.Task
import com.onecosys.get_things_done.exception.BadRequestException
import com.onecosys.get_things_done.exception.TaskNotFoundException
import com.onecosys.get_things_done.model.Priority
import com.onecosys.get_things_done.model.dto.TaskDto
import com.onecosys.get_things_done.model.request.TaskRequest
import com.onecosys.get_things_done.repository.TaskRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class TaskServiceTest {

    @RelaxedMockK
    private lateinit var mockRepository: TaskRepository

    @InjectMockKs
    private lateinit var objectUnderTest: TaskService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        // to be implemented
    }

    @Test
    fun `when all tasks get fetched then check if the given size is correct`() {
        val expectedTasks = listOf(Task(), Task())
        every { mockRepository.findAll() } returns expectedTasks.toMutableList()

        val actualList: List<TaskDto> = objectUnderTest.getAllTasks()
        assertThat(actualList.size).isEqualTo(expectedTasks.size)
    }

    @Test
    fun `when open tasks get fetched then check if the first property has true for isTaskOpen`() {
        val task1 = Task().apply { isTaskOpen = true }
        val expectedTasks = listOf(task1)
        every { mockRepository.queryAllOpenTasks() } returns expectedTasks.toMutableList()

        val actualList: List<TaskDto> = objectUnderTest.getAllOpenTasks()
        assertThat(actualList[0].isTaskOpen).isEqualTo(true)
    }

    @Test
    fun `when open tasks get fetched then check if the first property has false for isTaskOpen`() {
        val task1 = Task().apply { isTaskOpen = false }
        val expectedTasks = listOf(task1)
        every { mockRepository.queryAllClosedTasks() } returns expectedTasks.toMutableList()

        val actualList: List<TaskDto> = objectUnderTest.getAllClosedTasks()
        assertThat(actualList[0].isTaskOpen).isEqualTo(false)
    }

    @Test
    fun `when task gets created then check if it gets properly created`() {
        val taskRequest =
            TaskRequest(0, "test task", false, false, LocalDateTime.now(), null, null, "0d", 0, Priority.LOW)
        val task = Task()
        task.description = taskRequest.description
        task.isReminderSet = taskRequest.isReminderSet
        task.isTaskOpen = taskRequest.isTaskOpen
        task.createdOn = taskRequest.createdOn
        task.startedOn = taskRequest.startedOn
        task.finishedOn = taskRequest.finishedOn
        task.timeTaken = taskRequest.timeTaken

        every { mockRepository.save(any()) } returns task
        val actualTask: Task = objectUnderTest.createTask(taskRequest)

        assertThat(actualTask.description).isEqualTo(taskRequest.description)
    }

    @Test
    fun `when task gets created with non unique description then check for bad request exception`() {
        val taskRequest =
            TaskRequest(4, "feed the cat", false, false, LocalDateTime.now(), null, null, "0d", 0, Priority.LOW)

        every { mockRepository.doesDescriptionExist(any()) } returns true

        val exception = assertThrows<BadRequestException> {
            objectUnderTest.createTask(taskRequest)
        }

        assertThat(exception.message).isEqualTo("There is already a task with description: feed the cat")
    }

    @Test
    fun `when get task by id is called then expect a specific description`() {
        val task = Task()
        task.description = "getTaskById"
        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(any()) } returns task
        val taskId: Long = 1234

        val taskDto = objectUnderTest.getTaskById(taskId)

        assertThat(taskDto.description).isEqualTo("getTaskById")
    }

    @Test
    fun `when get task by id is called then expect a task not found exception`() {
        every { mockRepository.existsById(any()) } returns false

        val exception = assertThrows<TaskNotFoundException> { objectUnderTest.getTaskById(123) }

        assertThat(exception.message).isEqualTo("Task with ID: 123 does not exist!")
    }

    @Test
    fun `when delete task by id is called then check for return message`() {
        val taskId: Long = 1234

        every { mockRepository.existsById(any()) } returns true

        val deleteTaskMsg: String = objectUnderTest.deleteTask(taskId)

        assertThat(deleteTaskMsg).isEqualTo("Task with id: $taskId has been deleted.")
    }

    /*

    @Test
    fun `when update task is called with one argument then expect actual and expected task created on field is equal`() {
        val actualTask = Task()
        every { mockRepository.findTaskById(2) } returns actualTask
        every { mockRepository.save(any()) } returns actualTask

        val updateTaskRequest =
            TaskRequest(222, "test task", false, false, LocalDateTime.now(), null, null, "0d", 0, Priority.LOW)
        val expectedDTo = objectUnderTest.updateTask(updateTaskRequest)
        assertThat(actualTask.createdOn).isEqualTo(expectedDTo.createdOn)
    }

    @Test
    fun `when update task is called with id and request model as arguments then expect actual and expected task created on field is equal`() {
        val actualTask = Task()
        every { mockRepository.findTaskById(2) } returns actualTask
        every { mockRepository.save(any()) } returns actualTask

        val id = 222L
        val updateTaskRequest =
            TaskRequest(id, "test task", false, false, LocalDateTime.now(), null, null, "0d", 0, Priority.LOW)
        val expectedDTo = objectUnderTest.updateTask(id, updateTaskRequest)
        assertThat(actualTask.createdOn).isEqualTo(expectedDTo.createdOn)
    }
*/

}