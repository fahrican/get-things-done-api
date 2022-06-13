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
    private lateinit var repository: TaskRepository

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
        every { repository.findAll() } returns expectedTasks.toMutableList()

        val actualList: List<TaskDto> = objectUnderTest.getAllTasks()
        assertThat(actualList.size).isEqualTo(expectedTasks.size)
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

        every { repository.save(any()) } returns task
        val actualTask: Task = objectUnderTest.createTask(taskRequest)

        assertThat(actualTask.description).isEqualTo(taskRequest.description)
    }

    @Test
    fun `when get task by id is called then expect a task with id 2`() {
        val actualTask = Task()
        every { repository.findTaskById(2) } returns actualTask
        val expectedTaskDto = objectUnderTest.getTaskById(2)
        assertThat(actualTask.taskId).isEqualTo(expectedTaskDto.id)
    }

    @Test
    fun `when update task is called then expect actual and expected task created on field is equal`() {
        val actualTask = Task()
        every { repository.findTaskById(2) } returns actualTask
        every { repository.save(any()) } returns actualTask

        val updateTaskRequest =
            UpdateTaskRequest(222, "test task", false, false, LocalDateTime.now(), null, null, "0d", 0)
        val expectedDTo = objectUnderTest.updateTask(updateTaskRequest)
        assertThat(actualTask.createdOn).isEqualTo(expectedDTo.createdOn)
    }

    @Test
    fun `when delete task by id is called then check for return message`() {
        val actualTask = Task()
        every { repository.save(any()) } returns actualTask

        val actualText: String? = actualTask.taskId?.let { objectUnderTest.deleteTask(it) }
        val expectedText: String? = actualTask.taskId?.let { "Task with id: $it has been deleted." }
        assertThat(actualText).isEqualTo(expectedText)
    }

}