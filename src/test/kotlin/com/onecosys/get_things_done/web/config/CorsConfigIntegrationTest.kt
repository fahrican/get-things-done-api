package com.onecosys.get_things_done.web.config

import com.onecosys.get_things_done.web.rest.TaskController
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension::class)
@WebMvcTest()
internal class CorsConfigIntegrationTest(@Autowired private val mockMvc: MockMvc) {

    @Test
    fun test_corsFilterBean() {

        val result = mockMvc.perform(MockMvcRequestBuilders.get("/api/all-tasks"))

        result.andExpect(status().isOk)
        result.andExpect(header().string("Access-Control-Allow-Origin", "*"))
        result.andExpect(header().string("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE"))
        result.andExpect(header().string("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, X-Auth-Token, X-Csrf-Token, WWW-Authenticate, Authorization"))
        result.andExpect(header().string("Access-Control-Expose-Headers", "custom-token1, custom-token2"))
        result.andExpect(header().string("Access-Control-Allow-Credentials", "false"))
        result.andExpect(header().string("Access-Control-Max-Age", "3600"))
        result.andReturn()
    }

}