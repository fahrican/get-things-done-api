package com.onecosys.getthingsdone

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onecosys.getthingsdone.models.Priority
import com.onecosys.getthingsdone.models.TaskCreateRequest
import com.onecosys.getthingsdone.models.TaskFetchResponse
import com.onecosys.getthingsdone.task.service.TaskService
import com.onecosys.getthingsdone.task.util.AuthenticatedUserProvider
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.OffsetDateTime

@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@AutoConfigureMockMvc
class GetThingsDoneApplicationTest {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var mockService: TaskService

    @MockBean
    private lateinit var mockUserProvider: AuthenticatedUserProvider

    private val mapper = jacksonObjectMapper()

    companion object {

        @Container
        private val postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:15.4")

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
        }
    }

    @Test
    fun `when database is connected then it should be Postgres version 13`() {
        val actualDatabaseVersion = jdbcTemplate.queryForObject("SELECT version()", String::class.java)
        actualDatabaseVersion shouldContain "PostgreSQL 15.4"
    }


    @Test
    @WithMockUser(username = "testUser", roles = ["USER", "ADMIN"])
    fun shouldCreateTask() {
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
            priority = Priority.lOW
        )

        val request = TaskCreateRequest(
            description = "test test",
            isReminderSet = true,
            isTaskOpen = true,
            startedOn = null,
            finishedOn = null,
            timeInterval = "1 hour ",
            timeTaken = 2,
            priority = Priority.hIGH
        )

        Mockito.`when`(mockService.createTask(request, mockUserProvider.getUser())).thenReturn(taskFetchResponse)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isCreated())
    }
}