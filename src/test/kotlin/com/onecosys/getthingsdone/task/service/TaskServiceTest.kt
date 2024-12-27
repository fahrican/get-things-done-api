package com.onecosys.getthingsdone.task.service

import com.onecosys.getthingsdone.dto.Priority
import com.onecosys.getthingsdone.dto.TaskCreateRequest
import com.onecosys.getthingsdone.dto.TaskFetchResponse
import com.onecosys.getthingsdone.dto.TaskStatus
import com.onecosys.getthingsdone.dto.TaskUpdateRequest
import com.onecosys.getthingsdone.error.BadRequestException
import com.onecosys.getthingsdone.error.TaskNotFoundException
import com.onecosys.getthingsdone.task.entity.MAX_DESCRIPTION_LENGTH
import com.onecosys.getthingsdone.task.entity.MIN_DESCRIPTION_LENGTH
import com.onecosys.getthingsdone.task.entity.Task
import com.onecosys.getthingsdone.task.repository.TaskRepository
import com.onecosys.getthingsdone.task.util.TaskTimestamp
import com.onecosys.getthingsdone.task.util.converter.TaskMapper
import com.onecosys.getthingsdone.user.entity.AppUser
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Clock
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

@ExtendWith(MockKExtension::class)
internal class TaskServiceTest {

    private val mockRepository = mockk<TaskRepository>(relaxed = true)
    private val mockTaskTimestamp = mockk<TaskTimestamp>(relaxed = true)
    private val mockUser = mockk<AppUser>(relaxed = true)

    private val taskId: Long = 234
    private val date = LocalDate.of(2020, 12, 31)
    private var mapper = TaskMapper()

    private lateinit var clock: Clock
    private lateinit var task: Task
    private lateinit var createRequest: TaskCreateRequest
    private lateinit var objectUnderTest: TaskService
    private val updateRequest: TaskUpdateRequest
        get() {
            val updateRequest =
                TaskUpdateRequest(
                    task.description,
                    isReminderSet = false,
                    isTaskOpen = false,
                    startedOn = OffsetDateTime.now(
                        Clock.fixed(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
                    ),
                    finishedOn = OffsetDateTime.now(
                        Clock.fixed(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
                    ),
                    timeInterval = "0d",
                    timeTaken = 0,
                    priority = Priority.low
                )
            return updateRequest
        }

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
            priority = Priority.low
        )
        clock = Clock.fixed(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
        task = Task()
        objectUnderTest = TaskServiceImpl(mockRepository, mapper, mockTaskTimestamp)
    }

    @Test
    fun `when all tasks get fetched then check if the given size is correct`() {
        val expectedTasks = listOf(Task(), Task())

        every { mockRepository.findAllByUserOrderByIdAsc(any()) } returns expectedTasks.toMutableSet()
        val actualList: Set<TaskFetchResponse> = objectUnderTest.getTasks(mockUser, null)

        assertThat(actualList.size).isEqualTo(expectedTasks.size)
        verify(exactly = 1) { mockRepository.findAllByUserOrderByIdAsc(any()) }
    }

    @Test
    fun `when open tasks get fetched then check if the first property has true for isTaskOpen`() {
        task.isTaskOpen = true
        val expectedTasks = listOf(task)

        every {
            mockRepository.findAllByUserAndIsTaskOpenOrderByIdAsc(
                any(),
                true
            )
        } returns expectedTasks.toMutableSet()
        val actualList: Set<TaskFetchResponse> = objectUnderTest.getTasks(mockUser, TaskStatus.open)

        assertThat(actualList.elementAt(0).isTaskOpen).isEqualTo(task.isTaskOpen)
        verify(exactly = 1) { mockRepository.findAllByUserAndIsTaskOpenOrderByIdAsc(any(), true) }
    }

    @Test
    fun `when open tasks get fetched then check if the first property has false for isTaskOpen`() {
        task.isTaskOpen = false
        val expectedTasks = listOf(task)

        every {
            mockRepository.findAllByUserAndIsTaskOpenOrderByIdAsc(
                any(),
                false
            )
        } returns expectedTasks.toMutableSet()
        val actualList: Set<TaskFetchResponse> = objectUnderTest.getTasks(mockUser, TaskStatus.closed)

        assertThat(actualList.elementAt(0).isTaskOpen).isEqualTo(task.isTaskOpen)
    }

    @Test
    fun `when task gets created then check if it gets properly created`() {
        task = mapper.toEntity(createRequest, mockTaskTimestamp.createClockWithZone(), mockUser)

        every { mockTaskTimestamp.createClockWithZone() } returns Clock.fixed(
            date.atStartOfDay(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault()
        )
        every { mockRepository.save(any()) } returns task
        val actualTaskFetchResponse: TaskFetchResponse = objectUnderTest.createTask(createRequest, mockUser)

        assertThat(actualTaskFetchResponse.id).isEqualTo(task.id)
        assertThat(actualTaskFetchResponse.description).isEqualTo(createRequest.description)
        assertThat(actualTaskFetchResponse.isReminderSet).isEqualTo(task.isReminderSet)
        assertThat(actualTaskFetchResponse.isTaskOpen).isEqualTo(task.isTaskOpen)
        assertThat(actualTaskFetchResponse.startedOn).isEqualTo(task.startedOn)
        assertThat(actualTaskFetchResponse.finishedOn).isEqualTo(task.finishedOn)
        assertThat(actualTaskFetchResponse.timeInterval).isEqualTo(task.timeInterval)
        assertThat(actualTaskFetchResponse.timeTaken).isEqualTo(task.timeTaken)
        assertThat(actualTaskFetchResponse.priority).isEqualTo(task.priority)
        assertThat(actualTaskFetchResponse.createdOn).isEqualTo(task.createdOn)
    }

    @Test
    fun `when task gets created with non unique description then check for bad request exception`() {
        every { mockRepository.doesDescriptionExist(any()) } returns true
        val exception = assertThrows<BadRequestException> { objectUnderTest.createTask(createRequest, mockUser) }

        assertThat(exception.message).isEqualTo("A task with the description '${createRequest.description}' already exists")
        verify { mockRepository.save(any()) wasNot called }
    }

    @Test
    fun `when client wants to create a task with description more than 255 characters then check for bad request exception`() {
        val taskDescriptionTooLong = TaskCreateRequest(
            description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to,  took a galley of type and scrambled",
            isReminderSet = true,
            isTaskOpen = true,
            startedOn = OffsetDateTime.now(),
            finishedOn = OffsetDateTime.now(),
            timeInterval = "35d",
            timeTaken = 1,
            priority = Priority.medium
        )

        val exception =
            assertThrows<BadRequestException> { objectUnderTest.createTask(taskDescriptionTooLong, mockUser) }
        assertThat(exception.message).isEqualTo("Description must be between $MIN_DESCRIPTION_LENGTH and $MAX_DESCRIPTION_LENGTH characters in length")
        verify { mockRepository.save(any()) wasNot called }
    }

    @Test
    fun `when client wants to create a task with description less than 3 characters then check for bad request exception`() {
        val taskDescriptionTooShort = TaskCreateRequest(
            description = "ab",
            isReminderSet = false,
            isTaskOpen = false,
            startedOn = null,
            finishedOn = null,
            timeInterval = "0d",
            timeTaken = 0,
            priority = Priority.low
        )

        val exception =
            assertThrows<BadRequestException> { objectUnderTest.createTask(taskDescriptionTooShort, mockUser) }
        assertThat(exception.message).isEqualTo("Description must be between $MIN_DESCRIPTION_LENGTH and $MAX_DESCRIPTION_LENGTH characters in length")
        verify { mockRepository.save(any()) wasNot called }
    }

    @Test
    fun `when save task is called then check if argument could be captured`() {
        val taskSlot = slot<Task>()
        task.description = createRequest.description
        task.isReminderSet = createRequest.isReminderSet
        task.isTaskOpen = createRequest.isTaskOpen
        task.createdOn = OffsetDateTime.now(
            Clock.fixed(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
        )
        task.startedOn = createRequest.startedOn
        task.finishedOn = createRequest.finishedOn
        task.timeInterval = createRequest.timeInterval
        task.timeTaken = createRequest.timeTaken
        task.priority = createRequest.priority

        every { mockTaskTimestamp.createClockWithZone() } returns Clock.fixed(
            date.atStartOfDay(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault()
        )
        every { mockRepository.save(capture(taskSlot)) } returns task
        val actualTaskFetchResponse: TaskFetchResponse = objectUnderTest.createTask(createRequest, mockUser)

        verify { mockRepository.save(capture(taskSlot)) }
        assertThat(actualTaskFetchResponse.id).isEqualTo(taskSlot.captured.id)
        assertThat(actualTaskFetchResponse.description).isEqualTo(taskSlot.captured.description)
        assertThat(actualTaskFetchResponse.isReminderSet).isEqualTo(taskSlot.captured.isReminderSet)
        assertThat(actualTaskFetchResponse.isTaskOpen).isEqualTo(taskSlot.captured.isTaskOpen)
        assertThat(actualTaskFetchResponse.createdOn).isEqualTo(taskSlot.captured.createdOn)
        assertThat(actualTaskFetchResponse.startedOn).isEqualTo(taskSlot.captured.startedOn)
        assertThat(actualTaskFetchResponse.finishedOn).isEqualTo(taskSlot.captured.finishedOn)
        assertThat(actualTaskFetchResponse.timeInterval).isEqualTo(taskSlot.captured.timeInterval)
        assertThat(actualTaskFetchResponse.timeTaken).isEqualTo(taskSlot.captured.timeTaken)
        assertThat(actualTaskFetchResponse.priority).isEqualTo(taskSlot.captured.priority)
    }

    @Test
    fun `when get task by id is called then expect a specific description`() {
        task.description = "getTaskById"
        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskByIdAndUser(any(), any()) } returns task
        val taskDto = objectUnderTest.getTaskById(1234, mockUser)

        assertThat(taskDto.description).isEqualTo(task.description)
    }

    @Test
    fun `when get task by id is called then expect a task not found exception`() {
        val expectedException = TaskNotFoundException("Task with ID: $taskId does not exist!")
        every {
            mockRepository.findTaskByIdAndUser(
                any(),
                any()
            )
        } throws expectedException
        val actualException = assertThrows<TaskNotFoundException> { objectUnderTest.getTaskById(taskId, mockUser) }

        assertThat(actualException.message).isEqualTo(expectedException.message)
        verify { mockRepository.findTaskByIdAndUser(any(), any())?.wasNot(called) }
    }

    @Test
    fun `when find task by id is called then check if argument could be captured`() {
        val id: Long = 2345
        val taskIdSlot = slot<Long>()

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskByIdAndUser(capture(taskIdSlot), any()) } returns task
        objectUnderTest.getTaskById(id, mockUser)

        verify { mockRepository.findTaskByIdAndUser(capture(taskIdSlot), any()) }
        assertThat(taskIdSlot.captured).isEqualTo(id)
    }

    @Test
    fun `when delete task by id is called then check for return message`() {
        every { mockRepository.existsById(taskId) } returns true
        objectUnderTest.deleteTask(taskId, mockUser)

        assertThat(mockRepository.findAll().size).isEqualTo(0)
    }

    @Test
    fun `when delete by task id is called then check if argument could be captured`() {
        val taskIdSlot = slot<Long>()

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.deleteById(capture(taskIdSlot)) } returns Unit
        objectUnderTest.deleteTask(taskId, mockUser)

        verify { mockRepository.deleteById(capture(taskIdSlot)) }
        assertThat(taskIdSlot.captured).isEqualTo(taskId)
    }

    @Test
    fun `when delete task by id is called then expect task not found exception`() {
        val expectedException = TaskNotFoundException("Task with ID: $taskId does not exist!")
        every { mockRepository.findTaskByIdAndUser(any(), any()) } throws expectedException

        val actualException = assertThrows<TaskNotFoundException> { objectUnderTest.deleteTask(taskId, mockUser) }

        assertThat(actualException.message).isEqualTo(expectedException.message)
        verify(exactly = 0) { mockRepository.deleteById(any()) }
    }

    @Test
    fun `when update task is called with task request argument then expect matching fields for actual task`() {
        task.description = "test task"

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskByIdAndUser(any(), any()) } returns task
        every { mockRepository.save(any()) } returns task
        val actualTask = objectUnderTest.updateTask(task.id, updateRequest, mockUser)

        assertThat(actualTask.description).isEqualTo(task.description)
        assertThat(actualTask.isReminderSet).isEqualTo(task.isReminderSet)
        assertThat(actualTask.isTaskOpen).isEqualTo(task.isTaskOpen)
        assertThat(actualTask.createdOn).isEqualTo(task.createdOn)
        assertThat(actualTask.startedOn).isEqualTo(task.startedOn)
        assertThat(actualTask.finishedOn).isEqualTo(task.finishedOn)
        assertThat(actualTask.timeInterval).isEqualTo(task.timeInterval)
        assertThat(actualTask.timeTaken).isEqualTo(task.timeTaken)
        assertThat(actualTask.priority).isEqualTo(task.priority)
    }

    @Test
    fun `when update task is called with task request argument then expect task not found exception `() {
        val expectedException = TaskNotFoundException("Task with ID: $taskId does not exist!")
        every { mockRepository.findTaskByIdAndUser(any(), any()) } throws expectedException

        val actualException =
            assertThrows<TaskNotFoundException> { objectUnderTest.updateTask(taskId, updateRequest, mockUser) }

        assertThat(actualException.message).isEqualTo(expectedException.message)
        verify(exactly = 0) { mockRepository.save(any()) }
    }
}
