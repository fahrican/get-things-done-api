package com.onecosys.get_things_done.controller

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onecosys.get_things_done.dto.TaskDto
import com.onecosys.get_things_done.model.Task
import com.onecosys.get_things_done.request.CreateTaskRequest
import com.onecosys.get_things_done.request.UpdateTaskRequest
import com.onecosys.get_things_done.service.TaskService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
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
internal class TaskControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockBean
    private lateinit var mockService: TaskService

    private val mapper = jacksonObjectMapper()

    private val dummyDto1 = TaskDto(
        33, "test1",
        isReminderSet = false,
        isTaskOpen = false,
        createdOn = LocalDateTime.now(),
        startedOn = null,
        finishedOn = null,
        timeInterval = "1d",
        timeTaken = 1
    )

    @BeforeEach
    fun setUp() {
        mapper.registerModule(JavaTimeModule())
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `given tasks when fetch happen then check for size`() {
        val taskDto2 = TaskDto(
            44,
            "test2",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2
        )

        `when`(mockService.getAllTasks()).thenReturn(listOf(dummyDto1, taskDto2))

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks")).andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.size()").value(2))
    }

    @Test
    fun `given one task when get task by id is called then check for correct description`() {
        `when`(mockService.getTaskById(33)).thenReturn(dummyDto1)

        mockMvc.perform(MockMvcRequestBuilders.get("/task/${dummyDto1.id}"))
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.description").value("test1"))
    }

    @Test
    fun `given one task when get task by id is called with string instead of int then check for bad request`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/task/404L")).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `given create task request when task gets created then check for correct property`() {
        val request = CreateTaskRequest(
            "test2",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2
        )

        val task = Task()
        task.timeTaken = 2
        `when`(mockService.createTask(request)).thenReturn(task)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timeTaken").value(task.timeTaken))
    }

    @Test
    fun `given update task request when task gets updated then check for correct property`() {
        val request = UpdateTaskRequest(
            77,
            "update task",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            startedOn = null,
            finishedOn = null,
            timeInterval = "2d",
            timeTaken = 2
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
            timeTaken = 2
        )

        `when`(mockService.updateTask(request)).thenReturn(dummyDto)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.description").value(dummyDto.description))
    }
}