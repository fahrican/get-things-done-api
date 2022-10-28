package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.model.dto.TaskDto
import com.onecosys.get_things_done.entity.Task
import com.onecosys.get_things_done.exception.BadRequestException
import com.onecosys.get_things_done.model.Priority
import com.onecosys.get_things_done.model.request.TaskRequest
import com.onecosys.get_things_done.repository.TaskRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

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
        val taskRequest2 =
            TaskRequest(2, "test task", false, false, LocalDateTime.now(), null, null, "0d", 0, Priority.LOW)
        val task2 = Task()
        task2.description = taskRequest2.description
        task2.isReminderSet = taskRequest2.isReminderSet
        task2.isTaskOpen = taskRequest2.isTaskOpen
        task2.createdOn = taskRequest2.createdOn
        task2.startedOn = taskRequest2.startedOn
        task2.finishedOn = taskRequest2.finishedOn
        task2.timeTaken = taskRequest2.timeTaken

        every { mockRepository.save(any()) } throws BadRequestException("There is already a task with description: ${task2.description}")

        assertThrows<BadRequestException> {
        objectUnderTest.createTask(taskRequest2)
            objectUnderTest.createTask(
                TaskRequest(4, "test task", false, false, LocalDateTime.now(), null, null, "0d", 0, Priority.LOW)
            )
        }
    }

    @Test
    fun `when get task by id is called then expect a task with id 2`() {
        val actualTask = Task()
        every { mockRepository.findTaskById(2) } returns actualTask
        val expectedTaskDto = objectUnderTest.getTaskById(2)
        assertThat(actualTask.id).isEqualTo(expectedTaskDto.id)
    }

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

    @Test
    fun `when delete task by id is called then check for return message`() {
        val actualTask = Task()
        every { mockRepository.save(any()) } returns actualTask

        mockRepository.save(actualTask)

        val actualText: String = objectUnderTest.deleteTask(actualTask.id)
        val expectedText = "Task with id: ${actualTask.id} has been deleted."
        assertThat(actualText).isEqualTo(expectedText)
    }

}