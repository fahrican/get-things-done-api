package com.onecosys.get_things_done.repository

import com.onecosys.get_things_done.model.Task
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class TaskRepositoryTest {

    @Container
    private val mySQLContainer = MySQLContainer("mysql:latest")
        .withDatabaseName("heroku_4d803313a1afd6b")
        .withUsername("testuser")
        .withPassword("pass")
        .withReuse(true)


    @Autowired
    private lateinit var repository: TaskRepository
/*
    @Test
    fun shouldSaveTask() {
        val expectedTask = Task()
        val actualTask: Task = repository.save(expectedTask)
        assertThat(actualTask).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedTask)

    }*/
}