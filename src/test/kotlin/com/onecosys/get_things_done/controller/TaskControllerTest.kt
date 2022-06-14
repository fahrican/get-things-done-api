package com.onecosys.get_things_done.controller

import com.onecosys.get_things_done.dto.TaskDto
import com.onecosys.get_things_done.repository.TaskRepository
import com.onecosys.get_things_done.service.TaskService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity.status
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.shaded.org.hamcrest.Matchers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.LocalDateTime
import org.mockito.Mockito.`when`


@RunWith(SpringRunner::class)
@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [TaskController::class])
internal class TaskControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var mockRepository: TaskRepository

    @MockBean
    private lateinit var mockService: TaskService


    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `should fetch`() {
        val taskDto1 = TaskDto(33, "test1", false, false, LocalDateTime.now(), null, null, "1d", 1)
         val taskDto2 = TaskDto(44, "test2", false, false, LocalDateTime.now(), null, null, "2d", 2)

         `when`(mockService.getAllTasks()).thenReturn(listOf(taskDto1, taskDto2))

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks")).andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.size()").value(2))

        //verify(mockService.getAllTasks())
    }

    @Test
    fun `test tasks`() {
        val request: RequestBuilder = MockMvcRequestBuilders.get("/tasks")
        val result: MvcResult = mockMvc.perform(request).andReturn()
        assertEquals("test", result.response.contentAsString)
    }

}