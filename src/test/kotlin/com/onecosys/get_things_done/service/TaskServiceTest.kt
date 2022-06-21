package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.dto.TaskDto
import com.onecosys.get_things_done.model.Task
import com.onecosys.get_things_done.repository.TaskRepository
import com.onecosys.get_things_done.request.CreateTaskRequest
import com.onecosys.get_things_done.request.UpdateTaskRequest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
        val taskRequest = CreateTaskRequest("test task", false, false, LocalDateTime.now(), null, null, "0d", 0)
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
    fun `when get task by id is called then expect a task with id 2`() {
        val actualTask = Task()
        every { mockRepository.findTaskById(2) } returns actualTask
        val expectedTaskDto = objectUnderTest.getTaskById(2)
        assertThat(actualTask.id).isEqualTo(expectedTaskDto.id)
    }

    @Test
    fun `when update task is called then expect actual and expected task created on field is equal`() {
        val actualTask = Task()
        every { mockRepository.findTaskById(2) } returns actualTask
        every { mockRepository.save(any()) } returns actualTask

        val updateTaskRequest =
            UpdateTaskRequest(222, "test task", false, false, LocalDateTime.now(), null, null, "0d", 0)
        val expectedDTo = objectUnderTest.updateTask(updateTaskRequest)
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