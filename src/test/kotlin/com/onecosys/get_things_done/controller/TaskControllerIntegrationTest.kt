package com.onecosys.get_things_done.controller

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onecosys.get_things_done.model.dto.TaskDto
import com.onecosys.get_things_done.entity.Task
import com.onecosys.get_things_done.model.Priority
import com.onecosys.get_things_done.model.request.TaskRequest
import com.onecosys.get_things_done.service.TaskService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.LocalDateTime


@RunWith(SpringRunner::class)
@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [TaskController::class])
internal class TaskControllerIntegrationTest(@Autowired private val mockMvc: MockMvc) {

    @MockBean
    private lateinit var mockService: TaskService

    private val mapper = jacksonObjectMapper()

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

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `given all tasks when fetch happen then check for size`() {
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

        `when`(mockService.getAllTasks()).thenReturn(listOf(dummyDto1, taskDto2))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/all-tasks"))
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.size()").value(2))
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

        mockMvc.perform(MockMvcRequestBuilders.get("/api/open-tasks"))
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$[0].isTaskOpen").value(true))
    }

    @Test
    fun `given closed tasks when fetch happen then check for size  and isTaskOpen is false`() {
        `when`(mockService.getAllClosedTasks()).thenReturn(listOf(dummyDto1))

        mockMvc.perform(MockMvcRequestBuilders.get("/api/closed-tasks"))
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$[0].isTaskOpen").value(false))
    }

    @Test
    fun `given one task when get task by id is called then check for correct description`() {
        `when`(mockService.getTaskById(33)).thenReturn(dummyDto1)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/task/${dummyDto1.id}"))
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.description").value("test1"))
    }

    @Test
    fun `given one task when get task by id is called with string instead of int then check for bad request`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/task/404L"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `given create task request when task gets created then check for correct property`() {
        val request = TaskRequest(
            0,
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

        val task = Task()
        task.timeTaken = 2
        `when`(mockService.createTask(request)).thenReturn(task)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timeTaken").value(task.timeTaken))
    }

    @Test
    fun `given update task request when task gets updated then check for correct property`() {
        val request = TaskRequest(
            77,
            "update task",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2,
            priority = Priority.LOW
        )

        val dummyDto = TaskDto(
            44,
            request.description,
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2,
            priority = Priority.LOW
        )

        `when`(mockService.updateTask(request)).thenReturn(dummyDto)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.description").value(dummyDto.description))
    }

    @Test
    fun `given update task via id when task gets updated then check for correct property`() {
        val id = 77L
        val dummyRequest = TaskRequest(
            id,
            "update task",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2,
            priority = Priority.LOW
        )

        val dummyDto = TaskDto(
            44,
            dummyRequest.description,
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2,
            priority = Priority.LOW
        )

        `when`(mockService.updateTask(id, dummyRequest)).thenReturn(dummyDto)


        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/update/${id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dummyRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.description").value(dummyDto.description))
    }

    @Test
    fun `given id for delete request when delete task is performed then check for the message`() {
        val id = 33L
        val expectedMessage = "Task with id: $id has been deleted."

        `when`(mockService.deleteTask(id)).thenReturn(expectedMessage)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/delete/${id}")
        ).andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(content().string(expectedMessage))
    }

    @Test
    fun `given id for delete request when delete task is performed then check for the message1`() {
        val id: Long = 33
        val expectedMessage = "Task with id: $id has been deleted."

        `when`(mockService.deleteTask(id)).thenReturn(expectedMessage)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/delete")
                .contentType(MediaType.APPLICATION_JSON).param("id", "33")
        ).andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(content().string(expectedMessage))
    }

}