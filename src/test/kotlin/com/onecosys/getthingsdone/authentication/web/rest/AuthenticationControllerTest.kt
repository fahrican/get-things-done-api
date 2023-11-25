package com.onecosys.getthingsdone.authentication.web.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onecosys.getthingsdone.authentication.dto.AuthenticationRequest
import com.onecosys.getthingsdone.authentication.dto.AuthenticationResponse
import com.onecosys.getthingsdone.authentication.dto.EmailConfirmedResponse
import com.onecosys.getthingsdone.authentication.dto.RegisterRequest
import com.onecosys.getthingsdone.authentication.service.AuthenticationService
import com.onecosys.getthingsdone.authentication.service.JwtService
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

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [AuthenticationController::class])
@AutoConfigureMockMvc(addFilters = false)
internal class AuthenticationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var mockAuthenticationService: AuthenticationService

    @MockBean
    private lateinit var mockJwtService: JwtService

    private val dummyEmailConfirmResponse = EmailConfirmedResponse("success response")

    private val mapper = jacksonObjectMapper()


    @Test
    fun `when user sign up is triggered then expect success response`() {
        val registerRequest = RegisterRequest(
            firstName = "Mahmoud",
            lastName = "Darwoush",
            email = "mahmoud@aol.com",
            username = "mahmoud-darwoush",
            password = "123456",
            passwordConfirmation = "123456"
        )
        `when`(mockAuthenticationService.signUp(registerRequest)).thenReturn(dummyEmailConfirmResponse)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(registerRequest))
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isCreated)
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message").value(dummyEmailConfirmResponse.message))
    }

    @Test
    fun `when user verify is triggered then expect success response`() {
        val token = "a1b2c3"
        `when`(mockAuthenticationService.verifyUser(token)).thenReturn(dummyEmailConfirmResponse)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/auth/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", token)
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message").value(dummyEmailConfirmResponse.message))
    }

    @Test
    fun `when user sign in triggered then expect success response`() {
        val request = AuthenticationRequest(username = "mahmoud-darwoush", password = "123456")
        val response = AuthenticationResponse(accessToken = "acess123", refreshToken = "refresh123")
        `when`(mockAuthenticationService.signIn(request)).thenReturn(response)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value(response.accessToken))
    }

    @Test
    fun `when user password reset triggered then expect success response`() {
        val email = "mahmoud@aol.com"
        `when`(mockAuthenticationService.requestPasswordReset(email)).thenReturn(dummyEmailConfirmResponse)

        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/password-reset")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", email)
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
        resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message").value(dummyEmailConfirmResponse.message))
    }
}