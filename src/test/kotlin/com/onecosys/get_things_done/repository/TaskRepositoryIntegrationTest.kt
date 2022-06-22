package com.onecosys.get_things_done.repository

import com.onecosys.get_things_done.AbstractContainerBaseTest
import com.onecosys.get_things_done.model.Task
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
internal class TaskRepositoryIntegrationTest : AbstractContainerBaseTest() {

    @Autowired
    private lateinit var objectUnderTest: TaskRepository

    companion object {
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl)
            registry.add("spring.datasource.password", mySQLContainer::getPassword)
            registry.add("spring.datasource.username", mySQLContainer::getUsername)
        }
    }

    @Test
    fun `when task gets saved in database then check for proper id`() {
        val task = Task()
        task.description = "test for docker"
        val savedTask: Task = objectUnderTest.save(task)
        assertThat(savedTask).usingRecursiveComparison().ignoringFields("id").isEqualTo(task)
    }

/*
    @Test
    fun testForDocker() {
        val tasks = objectUnderTest.findAll()
        val firstTask = tasks[0]
        println(firstTask.description)
    }*/
}