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

}