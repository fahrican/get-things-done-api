package com.onecosys.get_things_done.web.rest

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onecosys.get_things_done.data.entity.Priority
import com.onecosys.get_things_done.data.model.dto.TaskDto
import com.onecosys.get_things_done.data.model.request.MAX_DESCRIPTION_LENGTH
import com.onecosys.get_things_done.data.model.request.MIN_DESCRIPTION_LENGTH
import com.onecosys.get_things_done.data.model.request.TaskCreateRequest
import com.onecosys.get_things_done.data.model.request.TaskUpdateRequest
import com.onecosys.get_things_done.error_handling.BadRequestException
import com.onecosys.get_things_done.error_handling.TaskNotFoundException
import com.onecosys.get_things_done.service.TaskService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.LocalDateTime


@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [TaskController::class])
internal class TaskControllerIntegrationTest(@Autowired private val mockMvc: MockMvc) {

    @MockBean
    private lateinit var mockService: TaskService

    private val mapper = jacksonObjectMapper()

    private val taskId: Long = 33
    private val dummyDto1 = TaskDto(
            33,
            "test1",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            startedOn = null,
            finishedOn = null,
            timeInterval = "1d",
            timeTaken = 1,
            priority = Priority.LOW
    )

    @BeforeEach
    fun setUp() {
        mapper.registerModule(JavaTimeModule())
    }

    @Test
    fun `given all tasks when fetch happen then check for size`() {
        // GIVEN
        val taskDto2 = TaskDto(
                44,
                "test2",
                isReminderSet = false,
                isTaskOpen = false,
                createdOn = LocalDateTime.now(),
                startedOn = null,
                finishedOn = null,
                timeInterval = "2d",
                timeTaken = 2,
                priority = Priority.LOW
        )
        val taskDtos = listOf(dummyDto1, taskDto2)

        // WHEN
        `when`(mockService.getAllTasks()).thenReturn(taskDtos)
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/all-tasks"))

        // THEN
        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.size()").value(taskDtos.size))
    }

    @Test
    fun `given open tasks when fetch happen then check for size and isTaskOpen is true`() {
        val taskDto2 = TaskDto(
                44,
                "test2",
                isReminderSet = false,
                isTaskOpen = true,
                createdOn = LocalDateTime.now(),
                startedOn = null,
                finishedOn = null,
                timeInterval = "2d",
                timeTaken = 2,
                priority = Priority.LOW
        )

        `when`(mockService.getAllOpenTasks()).thenReturn(listOf(taskDto2))
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/open-tasks"))

        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.size()").value(1))
        resultActions.andExpect(jsonPath("$[0].isTaskOpen").value(taskDto2.isTaskOpen))
    }

    @Test
    fun `given closed tasks when fetch happen then check for size  and isTaskOpen is false`() {
        // GIVEN
        // WHEN
        `when`(mockService.getAllClosedTasks()).thenReturn(listOf(dummyDto1))
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/closed-tasks"))

        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.size()").value(1))
        resultActions.andExpect(jsonPath("$[0].isTaskOpen").value(dummyDto1.isTaskOpen))
    }

    @Test
    fun `given one task when get task by id is called then check for correct description`() {
        `when`(mockService.getTaskById(33)).thenReturn(dummyDto1)
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/task/${dummyDto1.id}"))

        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.description").value(dummyDto1.description))
    }

    @Test
    fun `given one task when get task by id is called with string instead of int then check for internal server error`() {
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/task/404L"))

        resultActions.andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }

    @Test
    fun `given task id when id does not exist then check for task not found exception`() {
        val id: Long = 121

        `when`(mockService.deleteTask(id)).thenThrow(TaskNotFoundException("Task with ID: $id does not exist!"))
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/delete/${id}"))

        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound)
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
                priority = Priority.LOW
        )
        val badRequestException = BadRequestException("Description needs to be at least $MIN_DESCRIPTION_LENGTH characters long or maximum $MAX_DESCRIPTION_LENGTH")

        `when`(mockService.createTask(request)).thenThrow(badRequestException)
        val resultActions: ResultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/create")
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
                priority = Priority.LOW
        )
        val taskDto = TaskDto(
                0,
                "test for db",
                isReminderSet = false,
                isTaskOpen = false,
                createdOn = LocalDateTime.now(),
                startedOn = null,
                finishedOn = null,
                timeInterval = "2d",
                timeTaken = 2,
                priority = Priority.LOW
        )

        `when`(mockService.createTask(request)).thenReturn(taskDto)
        val resultActions: ResultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isCreated)
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.timeTaken").value(taskDto.timeTaken))
    }

    @Test
    fun `given update task request when task gets updated then check for correct property`() {
        val request = TaskUpdateRequest(
                "update task",
                isReminderSet = false,
                isTaskOpen = false,
                startedOn = null,
                finishedOn = null,
                timeInterval = "2d",
                timeTaken = 2,
                priority = Priority.LOW
        )
        val dummyDto = TaskDto(
                44,
                request.description ?: "",
                isReminderSet = false,
                isTaskOpen = false,
                createdOn = LocalDateTime.now(),
                startedOn = null,
                finishedOn = null,
                timeInterval = "2d",
                timeTaken = 2,
                priority = Priority.LOW
        )

        `when`(mockService.updateTask(dummyDto.id!!, request)).thenReturn(dummyDto)
        val resultActions: ResultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/update/${dummyDto.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.description").value(dummyDto.description))
    }

    @Test
    fun `given id for delete request when delete task is performed then check for the message`() {
        val expectedMessage = "Task with id: $taskId has been deleted."

        `when`(mockService.deleteTask(taskId)).thenReturn(expectedMessage)
        val resultActions: ResultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/delete/${taskId}"))

        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().string(expectedMessage))
    }

    @Test
    fun `given id for delete request when delete task is performed then check for the message1`() {
        val expectedMessage = "Task with id: $taskId has been deleted."

        `when`(mockService.deleteTask(taskId)).thenReturn(expectedMessage)
        val resultActions: ResultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", "33")
        )

        resultActions.andExpect(MockMvcResultMatchers.status().`is`(200))
        resultActions.andExpect(content().string(expectedMessage))
    }
}