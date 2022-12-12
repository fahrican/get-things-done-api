package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.data.entity.Task
import com.onecosys.get_things_done.exception.BadRequestException
import com.onecosys.get_things_done.exception.TaskNotFoundException
import com.onecosys.get_things_done.data.model.Priority
import com.onecosys.get_things_done.data.model.dto.TaskDto
import com.onecosys.get_things_done.data.model.request.TaskCreateRequest
import com.onecosys.get_things_done.repository.TaskRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
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

    private val task = Task()
    private lateinit var createRequest: TaskCreateRequest

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        createRequest = TaskCreateRequest(
            0,
            "test task",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            startedOn = null,
            finishedOn = null,
            timeInterval = "0d",
            timeTaken = 0,
            priority = Priority.LOW
        )
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
        task.isTaskOpen = true
        val expectedTasks = listOf(task)
        every { mockRepository.queryAllOpenTasks() } returns expectedTasks.toMutableList()
        val actualList: List<TaskDto> = objectUnderTest.getAllOpenTasks()

        assertThat(actualList[0].isTaskOpen).isEqualTo(true)
    }

    @Test
    fun `when open tasks get fetched then check if the first property has false for isTaskOpen`() {
        task.isTaskOpen = false
        val expectedTasks = listOf(task)
        every { mockRepository.queryAllClosedTasks() } returns expectedTasks.toMutableList()
        val actualList: List<TaskDto> = objectUnderTest.getAllClosedTasks()

        assertThat(actualList[0].isTaskOpen).isEqualTo(false)
    }

    @Test
    fun `when task gets created then check if it gets properly created`() {
        task.description = createRequest.description
        task.isReminderSet = createRequest.isReminderSet
        task.isTaskOpen = createRequest.isTaskOpen
        task.createdOn = createRequest.createdOn
        task.startedOn = createRequest.startedOn
        task.finishedOn = createRequest.finishedOn
        task.timeTaken = createRequest.timeTaken

        every { mockRepository.save(any()) } returns task
        val actualTask: Task = objectUnderTest.createTask(createRequest)

        assertThat(actualTask.description).isEqualTo(createRequest.description)
    }

    @Test
    fun `when task gets created with non unique description then check for bad request exception`() {
        every { mockRepository.doesDescriptionExist(any()) } returns true

        val exception = assertThrows<BadRequestException> {
            objectUnderTest.createTask(createRequest)
        }

        assertThat(exception.message).isEqualTo("There is already a task with description: test task")
        verify { mockRepository.save(any()) wasNot called }
    }

    @Test
    fun `when save task is called then check if argument could be captured`() {
        val taskSlot = slot<Task>()
        task.description = createRequest.description
        task.isReminderSet = createRequest.isReminderSet
        task.isTaskOpen = createRequest.isTaskOpen
        task.createdOn = createRequest.createdOn
        task.startedOn = createRequest.startedOn
        task.finishedOn = createRequest.finishedOn
        task.timeTaken = createRequest.timeTaken

        every { mockRepository.save(capture(taskSlot)) } returns task
        val actualTask: Task = objectUnderTest.createTask(createRequest)

        verify { mockRepository.save(capture(taskSlot)) }
        assertThat(taskSlot.captured.id).isEqualTo(actualTask.id)
        assertThat(taskSlot.captured.description).isEqualTo(actualTask.description)
        assertThat(taskSlot.captured.priority).isEqualTo(actualTask.priority)
    }

    @Test
    fun `when get task by id is called then expect a specific description`() {
        task.description = "getTaskById"
        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(any()) } returns task
        val taskDto = objectUnderTest.getTaskById(1234)

        assertThat(taskDto.description).isEqualTo("getTaskById")
    }

    @Test
    fun `when get task by id is called then expect a task not found exception`() {
        every { mockRepository.existsById(any()) } returns false

        val exception = assertThrows<TaskNotFoundException> { objectUnderTest.getTaskById(123) }

        assertThat(exception.message).isEqualTo("Task with ID: 123 does not exist!")
        verify { mockRepository.findTaskById(any()) wasNot called }
    }

    @Test
    fun `when find task by id is called then check if argument could be captured`() {
        val taskIdSlot = slot<Long>()

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(capture(taskIdSlot)) } returns task
        objectUnderTest.getTaskById(2345)

        verify { mockRepository.findTaskById(capture(taskIdSlot)) }
        assertThat(taskIdSlot.captured).isEqualTo(2345)
    }

    @Test
    fun `when delete task by id is called then check for return message`() {
        val taskId: Long = 1234

        every { mockRepository.existsById(any()) } returns true
        val deleteTaskMsg: String = objectUnderTest.deleteTask(taskId)

        assertThat(deleteTaskMsg).isEqualTo("Task with id: $taskId has been deleted.")
    }


    @Test
    fun `when delete by task id is called then check if argument could be captured`() {
        val taskIdSlot = slot<Long>()

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.deleteById(capture(taskIdSlot)) } returns Unit
        objectUnderTest.deleteTask(234)

        verify { mockRepository.deleteById(capture(taskIdSlot)) }
        assertThat(taskIdSlot.captured).isEqualTo(234)
    }

    @Test
    fun `when update task is called with task request argument then expect specific description fpr actual task`() {
        task.description = "test task"
        val createRequest =
            TaskCreateRequest(
                task.id,
                task.description,
                isReminderSet = false,
                isTaskOpen = false,
                createdOn = LocalDateTime.now(),
                startedOn = null,
                finishedOn = null,
                timeInterval = "0d",
                timeTaken = 0,
                priority = Priority.LOW
            )

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(any()) } returns task
        every { mockRepository.save(any()) } returns task
        val actualTask = objectUnderTest.updateTask(createRequest)

        assertThat(actualTask.description).isEqualTo("test task")
    }

    @Test
    fun `when update task is called with task request argument then expect bad request exception`() {
        val createRequest: TaskCreateRequest? = null

        val exception = assertThrows<BadRequestException> { objectUnderTest.updateTask(createRequest) }

        assertThat(exception.message).isEqualTo("Update task failed!")
        verify { mockRepository.save(any()) wasNot called }
    }
}