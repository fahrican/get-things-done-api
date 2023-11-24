package com.onecosys.getthingsdone.user.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onecosys.getthingsdone.task.util.AuthenticatedUserProvider
import com.onecosys.getthingsdone.user.model.dto.UserInfoResponse
import com.onecosys.getthingsdone.user.model.dto.UserInfoUpdateRequest
import com.onecosys.getthingsdone.user.model.dto.UserPasswordUpdateRequest
import com.onecosys.getthingsdone.user.service.UserService
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
import java.security.Principal

@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@AutoConfigureMockMvc
internal class UserControllerIntegrationTest {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var mockUserProvider: AuthenticatedUserProvider

    @MockBean
    private lateinit var mockService: UserService

    @MockBean
    private lateinit var mockPrincipal: Principal

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
    @WithMockUser(username = "testUser", roles = ["USER", "ADMIN"])
    fun `when user info is requested then return success response`() {
        `when`(mockService.fetchInfo(mockPrincipal)).thenReturn(userResponse)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
        )
        resultActions.andExpect((MockMvcResultMatchers.status().isOk))
    }

    @Test
    @WithMockUser(username = "testUser", roles = ["USER", "ADMIN"])
    fun `when change user email is triggered then return success response`() {
        val email = "hello@aon.at"
        val request = HashMap<String, String>()
        request["email"] = email
        `when`(mockService.changeEmail(request, mockPrincipal)).thenReturn(userResponse)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/user/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
        resultActions.andExpect((MockMvcResultMatchers.status().isOk))
    }

    @Test
    @WithMockUser(username = "testUser", roles = ["USER", "ADMIN"])
    fun `when change username is triggered then return success response`() {
        val email = "ali-aziz"
        val request = HashMap<String, String>()
        request["username"] = email
        `when`(mockService.changeEmail(request, mockPrincipal)).thenReturn(userResponse)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/user/username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
        resultActions.andExpect((MockMvcResultMatchers.status().isOk))
    }

    @Test
    @WithMockUser(username = "testUser", roles = ["USER", "ADMIN"])
    fun `when change user password is triggered then return success response`() {
        val passwordRequest = UserPasswordUpdateRequest("oldPassword", "newPassword", "newPassword")
        doNothing().`when`(mockService).changePassword(passwordRequest, mockPrincipal)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/user/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(passwordRequest))
        )
        resultActions.andExpect((MockMvcResultMatchers.status().isOk))
    }

    @Test
    @WithMockUser(username = "testUser", roles = ["USER", "ADMIN"])
    fun `when change user info is triggered then return success response`() {
        val updateRequest = UserInfoUpdateRequest(firstName = "Omar", lastName = "Ramadan")
        `when`(mockService.changeInfo(updateRequest, mockPrincipal)).thenReturn(userResponse)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/v1/user/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateRequest))
        )
        resultActions.andExpect((MockMvcResultMatchers.status().isOk))
    }
}