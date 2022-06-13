package com.onecosys.get_things_done.repository

import com.onecosys.get_things_done.model.Task
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql

@DataJpaTest
@ActiveProfiles("test")
internal class TaskRepositoryTestEmbedded {

    @Autowired
    private lateinit var repository: TaskRepository

    @Test
    fun `when task is saved then check if it is properly saved`() {
        val task = Task()
        val savedTask: Task = repository.save(task)
        assertThat(savedTask).usingRecursiveComparison().ignoringFields("id").isEqualTo(task)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then check if it is not null`() {
            val task: Task = repository.findTaskById(111)
            assertThat(task).isNotNull
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then check for the number of tasks`() {
            val tasks: List<Task> = repository.findAll()
        assertThat(tasks.size).isEqualTo(2)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then remove it by id`() {
        val tasks: List<Task> = repository.findAll()
        repository.deleteById(222)
        assertThat(tasks.size).isEqualTo(2)
    }
}