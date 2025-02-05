package com.onecosys.getthingsdone.user.web.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onecosys.getthingsdone.dto.UserInfoResponse
import com.onecosys.getthingsdone.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.dto.UserPasswordUpdateRequest
import com.onecosys.getthingsdone.security.application.ClientSessionService
import com.onecosys.getthingsdone.user.application.AppUserService
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
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
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@AutoConfigureMockMvc
@WithMockUser(username = "testUser", roles = ["USER", "ADMIN"])
internal class AppUserControllerIT {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var mockUserProvider: ClientSessionService

    @MockBean
    private lateinit var mockService: AppUserService

    private val mapper = jacksonObjectMapper()

    private val userResponse =
        UserInfoResponse(firstName = "Omar", lastName = "Ramadan", email = "omar@aon.at", username = "omar030")

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
    fun `when user info is requested then return success response`() {
        `when`(mockService.fetchInfo()).thenReturn(userResponse)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
        )
        resultActions.andExpect((MockMvcResultMatchers.status().isOk))
    }

    @Test
    fun `when change user email is triggered then return success response`() {
        val email = "hello@aon.at"
        val request = HashMap<String, String>()
        request["email"] = email
        `when`(mockService.changeEmail(request)).thenReturn(userResponse)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/user/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
        resultActions.andExpect((MockMvcResultMatchers.status().isOk))
    }

    @Test
    fun `when change username is triggered then return success response`() {
        val email = "ali-aziz"
        val request = HashMap<String, String>()
        request["username"] = email
        `when`(mockService.changeEmail(request)).thenReturn(userResponse)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/user/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
        resultActions.andExpect((MockMvcResultMatchers.status().isOk))
    }

    @Test
    fun `when change user password is triggered then return success response`() {
        val passwordRequest = UserPasswordUpdateRequest("oldPassword", "newPassword", "newPassword")
        doNothing().`when`(mockService).changePassword(passwordRequest)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/user/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(passwordRequest))
        )
        resultActions.andExpect((MockMvcResultMatchers.status().isOk))
    }

    @Test
    fun `when change user info is triggered then return success response`() {
        val updateRequest = UserInfoUpdateRequest(firstName = "Omar", lastName = "Ramadan")
        `when`(mockService.changeInfo(updateRequest)).thenReturn(userResponse)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/user/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateRequest))
        )
        resultActions.andExpect((MockMvcResultMatchers.status().isOk))
    }
}