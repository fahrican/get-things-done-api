package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.model.entity.Priority
import com.onecosys.get_things_done.model.entity.Task
import com.onecosys.get_things_done.model.dto.TaskDto
import com.onecosys.get_things_done.error_handling.BadRequestException
import com.onecosys.get_things_done.error_handling.TaskNotFoundException
import com.onecosys.get_things_done.model.request.*
import com.onecosys.get_things_done.repository.TaskRepository
import com.onecosys.get_things_done.util.converter.TaskMapper
import com.onecosys.get_things_done.util.TaskTimestamp
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.verify
import io.mockk.called
import io.mockk.slot
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Clock
import java.time.ZoneId


@ExtendWith(MockKExtension::class)
internal class TaskServiceTest {

    @RelaxedMockK
    private lateinit var mockRepository: TaskRepository

    @RelaxedMockK
    private lateinit var taskTimestamp: TaskTimestamp

    private val taskId: Long = 234
    private val date = LocalDate.of(2020, 12, 31)

    private var mapper = TaskMapper()

    private lateinit var clock: Clock

    private lateinit var task: Task
    private lateinit var createRequest: TaskCreateRequest
    private lateinit var objectUnderTest: TaskService


    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        createRequest = TaskCreateRequest(
            "test task",
            isReminderSet = false,
            isTaskOpen = false,
            startedOn = null,
            finishedOn = null,
            timeInterval = "0d",
            timeTaken = 0,
            priority = Priority.LOW
        )
        clock = Clock.fixed(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
        task = Task()
        objectUnderTest = TaskServiceImpl(mockRepository, mapper, taskTimestamp)
    }

    @Test
    fun `when all tasks get fetched then check if the given size is correct`() {
        val expectedTasks = listOf(Task(), Task())

        every { mockRepository.findAllByOrderByIdAsc() } returns expectedTasks.toMutableSet()
        val actualList: Set<TaskDto> = objectUnderTest.getTasks(null)

        assertThat(actualList.size).isEqualTo(expectedTasks.size)
    }

    @Test
    fun `when open tasks get fetched then check if the first property has true for isTaskOpen`() {
        task.isTaskOpen = true
        val expectedTasks = listOf(task)

        every { mockRepository.findAllByIsTaskOpenOrderByIdAsc(true) } returns expectedTasks.toMutableSet()
        val actualList: Set<TaskDto> = objectUnderTest.getTasks(TaskStatus.OPEN)

        assertThat(actualList.elementAt(0).isTaskOpen).isEqualTo(task.isTaskOpen)
    }

    @Test
    fun `when open tasks get fetched then check if the first property has false for isTaskOpen`() {
        task.isTaskOpen = false
        val expectedTasks = listOf(task)

        every { mockRepository.findAllByIsTaskOpenOrderByIdAsc(false) } returns expectedTasks.toMutableSet()
        val actualList: Set<TaskDto> = objectUnderTest.getTasks(TaskStatus.CLOSED)

        assertThat(actualList.elementAt(0).isTaskOpen).isEqualTo(task.isTaskOpen)
    }

    @Test
    fun `when task gets created then check if it gets properly created`() {
        task.description = createRequest.description
        task.isReminderSet = createRequest.isReminderSet
        task.isTaskOpen = createRequest.isTaskOpen
        task.startedOn = createRequest.startedOn
        task.finishedOn = createRequest.finishedOn
        task.timeInterval = createRequest.timeInterval
        task.timeTaken = createRequest.timeTaken
        task.priority = createRequest.priority

        every { taskTimestamp.createClockWithZone() } returns Clock.fixed(
            date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault()
        )
        every { mockRepository.save(any()) } returns task
        val actualTaskDto: TaskDto = objectUnderTest.createTask(createRequest)

        assertThat(actualTaskDto.id).isEqualTo(task.id)
        assertThat(actualTaskDto.description).isEqualTo(createRequest.description)
        assertThat(actualTaskDto.isReminderSet).isEqualTo(task.isReminderSet)
        assertThat(actualTaskDto.isTaskOpen).isEqualTo(task.isTaskOpen)
        assertThat(actualTaskDto.startedOn).isEqualTo(task.startedOn)
        assertThat(actualTaskDto.finishedOn).isEqualTo(task.finishedOn)
        assertThat(actualTaskDto.timeInterval).isEqualTo(task.timeInterval)
        assertThat(actualTaskDto.timeTaken).isEqualTo(task.timeTaken)
        assertThat(actualTaskDto.priority).isEqualTo(task.priority)
        assertThat(actualTaskDto.createdOn).isEqualTo(task.createdOn)
    }

    @Test
    fun `when task gets created with non unique description then check for bad request exception`() {
        every { mockRepository.existsByDescription(any()) } returns true
        val exception = assertThrows<BadRequestException> { objectUnderTest.createTask(createRequest) }

        assertThat(exception.message).isEqualTo("A task with the description '${createRequest.description}' already exists")
        verify { mockRepository.save(any()) wasNot called }
    }

    @Test
    fun `when client wants to create a task with description more than 255 characters then check for bad request exception`() {
        val taskRequest = TaskCreateRequest(
            description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to,  took a galley of type and scrambled",
            isReminderSet = false,
            isTaskOpen = false,
            startedOn = null,
            finishedOn = null,
            timeInterval = "0d",
            timeTaken = 0,
            priority = Priority.LOW
        )

        val exception = assertThrows<BadRequestException> { objectUnderTest.createTask(taskRequest) }
        assertThat(exception.message).isEqualTo("Description must be between $MIN_DESCRIPTION_LENGTH and $MAX_DESCRIPTION_LENGTH characters in length")
        verify { mockRepository.save(any()) wasNot called }
    }

    @Test
    fun `when client wants to create a task with description less than 3 characters then check for bad request exception`() {
        val taskRequest = TaskCreateRequest(
            description = "ab",
            isReminderSet = false,
            isTaskOpen = false,
            startedOn = null,
            finishedOn = null,
            timeInterval = "0d",
            timeTaken = 0,
            priority = Priority.LOW
        )

        val exception = assertThrows<BadRequestException> { objectUnderTest.createTask(taskRequest) }
        assertThat(exception.message).isEqualTo("Description must be between $MIN_DESCRIPTION_LENGTH and $MAX_DESCRIPTION_LENGTH characters in length")
        verify { mockRepository.save(any()) wasNot called }
    }

    @Test
    fun `when save task is called then check if argument could be captured`() {
        val taskSlot = slot<Task>()
        task.description = createRequest.description
        task.isReminderSet = createRequest.isReminderSet
        task.isTaskOpen = createRequest.isTaskOpen
        task.createdOn = LocalDateTime.now(
            Clock.fixed(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
        )
        task.startedOn = createRequest.startedOn
        task.finishedOn = createRequest.finishedOn
        task.timeInterval = createRequest.timeInterval
        task.timeTaken = createRequest.timeTaken
        task.priority = createRequest.priority

        every { taskTimestamp.createClockWithZone() } returns Clock.fixed(
            date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault()
        )
        every { mockRepository.save(capture(taskSlot)) } returns task
        val actualTaskDto: TaskDto = objectUnderTest.createTask(createRequest)

        verify { mockRepository.save(capture(taskSlot)) }
        assertThat(actualTaskDto.id).isEqualTo(taskSlot.captured.id)
        assertThat(actualTaskDto.description).isEqualTo(taskSlot.captured.description)
        assertThat(actualTaskDto.isReminderSet).isEqualTo(taskSlot.captured.isReminderSet)
        assertThat(actualTaskDto.isTaskOpen).isEqualTo(taskSlot.captured.isTaskOpen)
        assertThat(actualTaskDto.createdOn).isEqualTo(taskSlot.captured.createdOn)
        assertThat(actualTaskDto.startedOn).isEqualTo(taskSlot.captured.startedOn)
        assertThat(actualTaskDto.finishedOn).isEqualTo(taskSlot.captured.finishedOn)
        assertThat(actualTaskDto.timeInterval).isEqualTo(taskSlot.captured.timeInterval)
        assertThat(actualTaskDto.timeTaken).isEqualTo(taskSlot.captured.timeTaken)
        assertThat(actualTaskDto.priority).isEqualTo(taskSlot.captured.priority)
    }

    @Test
    fun `when get task by id is called then expect a specific description`() {
        task.description = "getTaskById"
        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(any()) } returns task
        val taskDto = objectUnderTest.getTaskById(1234)

        assertThat(taskDto.description).isEqualTo(task.description)
    }

    @Test
    fun `when get task by id is called then expect a task not found exception`() {
        every { mockRepository.existsById(any()) } returns false
        val exception = assertThrows<TaskNotFoundException> { objectUnderTest.getTaskById(taskId) }

        assertThat(exception.message).isEqualTo("Task with ID: $taskId does not exist!")
        verify { mockRepository.findTaskById(any()) wasNot called }
    }

    @Test
    fun `when find task by id is called then check if argument could be captured`() {
        val id: Long = 2345
        val taskIdSlot = slot<Long>()

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(capture(taskIdSlot)) } returns task
        objectUnderTest.getTaskById(id)

        verify { mockRepository.findTaskById(capture(taskIdSlot)) }
        assertThat(taskIdSlot.captured).isEqualTo(id)
    }

    @Test
    fun `when delete task by id is called then check for return message`() {
        every { mockRepository.existsById(any()) } returns true
        val deleteTaskMsg: String = objectUnderTest.deleteTask(taskId)

        assertThat(deleteTaskMsg).isEqualTo("Task with id: $taskId has been deleted.")
    }


    @Test
    fun `when delete by task id is called then check if argument could be captured`() {
        val taskIdSlot = slot<Long>()

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.deleteById(capture(taskIdSlot)) } returns Unit
        objectUnderTest.deleteTask(taskId)

        verify { mockRepository.deleteById(capture(taskIdSlot)) }
        assertThat(taskIdSlot.captured).isEqualTo(taskId)
    }

    @Test
    fun `when update task is called with task request argument then expect specific description fpr actual task`() {
        task.description = "test task"
        val updateRequest =
            TaskUpdateRequest(
                task.description,
                isReminderSet = false,
                isTaskOpen = false,
                startedOn = null,
                finishedOn = null,
                timeInterval = "0d",
                timeTaken = 0,
                priority = Priority.LOW
            )

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(any()) } returns task
        every { mockRepository.save(any()) } returns task
        val actualTask = objectUnderTest.updateTask(task.id, updateRequest)

        assertThat(actualTask.description).isEqualTo(task.description)
    }
}