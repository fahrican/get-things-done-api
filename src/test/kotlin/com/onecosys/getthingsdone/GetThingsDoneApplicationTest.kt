package com.onecosys.getthingsdone

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onecosys.getthingsdone.model.Priority
import com.onecosys.getthingsdone.model.dto.TaskCreateRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.util.function.Supplier


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class GetThingsDoneApplicationTest(
    @Autowired private val mockMvc: MockMvc
) {

    private val mapper = jacksonObjectMapper()

    companion object {
        @Container
        val mongoDBContainer = MongoDBContainer("mongo:4.4.2")

        @JvmStatic
        @DynamicPropertySource
        fun datasourceConfig(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri", Supplier<Any> { mongoDBContainer.replicaSetUrl })
        }
    }

    @Test
    fun shouldCreateTask() {
        val request = TaskCreateRequest(
            "test test",
            true,
            true,
            LocalDateTime.now(),
            null,
            "1 hour ",
            2,
            Priority.HIGH
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isCreated())
    }


}