package com.onecosys.getthingsdone.task.web.rest

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onecosys.getthingsdone.dto.Priority
import com.onecosys.getthingsdone.dto.TaskCreateRequest
import com.onecosys.getthingsdone.dto.TaskFetchResponse
import com.onecosys.getthingsdone.dto.TaskStatus
import com.onecosys.getthingsdone.dto.TaskUpdateRequest
import com.onecosys.getthingsdone.security.application.ClientSessionService
import com.onecosys.getthingsdone.security.application.JwtService
import com.onecosys.getthingsdone.shared.error.BadRequestException
import com.onecosys.getthingsdone.shared.error.TaskNotFoundException
import com.onecosys.getthingsdone.task.application.TaskService
import com.onecosys.getthingsdone.task.domain.MAX_DESCRIPTION_LENGTH
import com.onecosys.getthingsdone.task.domain.MIN_DESCRIPTION_LENGTH
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [TaskController::class])
@AutoConfigureMockMvc(addFilters = false)
internal class TaskControllerIT(@Autowired private val mockMvc: MockMvc) {

    @MockBean
    private lateinit var mockService: TaskService

    @MockBean
    private lateinit var mockJwtService: JwtService

    @MockBean
    private lateinit var mockUserProvider: ClientSessionService

    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    private val taskId: Long = 33

    private val dummyDto = TaskFetchResponse(
        id = 33,
        description = "test1",
        isReminderSet = false,
        isTaskOpen = false,
        createdOn = OffsetDateTime.now(),
        startedOn = null,
        finishedOn = null,
        timeInterval = "1d",
        timeTaken = 1,
        priority = Priority.low
    )


    @Test
    fun `given all tasks when fetch happen then check for size`() {
        // GIVEN
        val taskFetchResponse2 = TaskFetchResponse(
            id = 44,
            description = "test2",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = OffsetDateTime.now(),
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2,
            priority = Priority.low
        )
        val taskDtos = setOf(dummyDto, taskFetchResponse2)

        // WHEN
        `when`(mockService.getTasks(mockUserProvider.getAuthenticatedUser(), null)).thenReturn(taskDtos)

        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks"))

        // THEN
        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(taskDtos.size))
    }

    @Test
    fun `given open tasks when fetch happen then check for size and isTaskOpen is true`() {
        val taskFetchResponse2 = TaskFetchResponse(
            id = 44,
            description = "test2",
            isReminderSet = false,
            isTaskOpen = true,
            createdOn = OffsetDateTime.now(),
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2,
            priority = Priority.low
        )

        `when`(mockService.getTasks(mockUserProvider.getAuthenticatedUser(), TaskStatus.open)).thenReturn(
            setOf(
                taskFetchResponse2
            )
        )
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks?status=open"))

        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[0].isTaskOpen").value(taskFetchResponse2.isTaskOpen))
    }

    @Test
    fun `given closed tasks when fetch happen then check for size  and isTaskOpen is false`() {
        // GIVEN
        // WHEN
        `when`(mockService.getTasks(mockUserProvider.getAuthenticatedUser(), TaskStatus.closed)).thenReturn(
            setOf(
                dummyDto
            )
        )
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks?status=closed"))

        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$[0].isTaskOpen").value(dummyDto.isTaskOpen))
    }

    @Test
    fun `given one task when get task by id is called then check for correct description`() {
        `when`(mockService.getTaskById(33, mockUserProvider.getAuthenticatedUser())).thenReturn(dummyDto)
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/${dummyDto.id}"))

        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(dummyDto.description))
    }

    @Test
    fun `given one task when get task by id is called with string instead of int then check for internal server error`() {
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/404L"))

        resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }

    @Test
    fun `given task id when id does not exist then check for task not found exception`() {
        val id: Long = 121

        `when`(
            mockService.deleteTask(
                id,
                mockUserProvider.getAuthenticatedUser()
            )
        ).thenThrow(TaskNotFoundException("Task with ID: $id does not exist!"))
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/$id"))

        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    fun `given task creation when description is the same to another then check for bad request exception`() {
        val request = TaskCreateRequest(
            description = "t",
            isReminderSet = false,
            isTaskOpen = false,
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2,
            priority = Priority.low
        )
        val badRequestException =
            BadRequestException("Description needs to be at least $MIN_DESCRIPTION_LENGTH characters long or maximum $MAX_DESCRIPTION_LENGTH")

        `when`(mockService.createTask(request, mockUserProvider.getAuthenticatedUser())).thenThrow(badRequestException)
        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `given create task request when task gets created then check for correct property`() {
        val request = TaskCreateRequest(
            description = "test for db",
            isReminderSet = false,
            isTaskOpen = false,
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2,
            priority = Priority.low
        )
        val taskFetchResponse = TaskFetchResponse(
            id = 0,
            description = "test for db",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = OffsetDateTime.now(),
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2,
            priority = Priority.low
        )

        `when`(mockService.createTask(request, mockUserProvider.getAuthenticatedUser())).thenReturn(taskFetchResponse)
        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isCreated)
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.timeTaken").value(taskFetchResponse.timeTaken))
    }

    @Test
    fun `given update task request when task gets updated then check for correct property`() {
        val str = "1986-04-08 12:30"
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val localDateTime = LocalDateTime.parse(str, formatter)
        val dateTime = localDateTime.atOffset(ZoneOffset.UTC)

        val request = TaskUpdateRequest(
            "update task",
            isReminderSet = false,
            isTaskOpen = false,
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2,
            priority = Priority.low
        )
        val dummyDto = TaskFetchResponse(
            id = 44,
            description = request.description ?: "",
            isReminderSet = true,
            isTaskOpen = true,
            createdOn = dateTime,
            startedOn = dateTime,
            finishedOn = dateTime,
            timeInterval = "3d",
            timeTaken = 3,
            priority = Priority.medium
        )

        `when`(mockService.updateTask(dummyDto.id!!, request, mockUserProvider.getAuthenticatedUser())).thenReturn(
            dummyDto
        )
        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/tasks/${dummyDto.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(dummyDto.description))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.isReminderSet").value(dummyDto.isReminderSet))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.isTaskOpen").value(dummyDto.isTaskOpen))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.timeInterval").value(dummyDto.timeInterval))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.timeTaken").value(dummyDto.timeTaken))
    }

    @Test
    fun `given id for delete request when delete task is performed then check for the message`() {
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/$taskId"))

        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent)
    }
}
