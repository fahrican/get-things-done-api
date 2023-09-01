package com.onecosys.getthingsdone.repository

import com.onecosys.getthingsdone.model.entity.Task
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql

@DataJpaTest(properties = ["spring.jpa.properties.javax.persistence.validation.mode=none"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class TaskRepositoryTestEmbedded {

    @Autowired
    private lateinit var objectUnderTest: TaskRepository

    private val numberOfRecordsInTestDataSql = 3
    private val numberOfClosedTasksInTestDataSql = 2
    private val numberOfOpenTasksInTestDataSql = 1

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then check if it is not null`() {
        val task: Task = objectUnderTest.findTaskById(111)
        assertThat(task).isNotNull
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then check for the number of tasks`() {
        val tasks: List<Task> = objectUnderTest.findAll()
        assertThat(tasks.size).isEqualTo(numberOfRecordsInTestDataSql)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then remove it by id`() {
        objectUnderTest.deleteById(222)
        val tasks: List<Task> = objectUnderTest.findAll()
        assertThat(tasks.size).isEqualTo(2)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then check for the number of open tasks`() {
        val tasks: Set<Task> = objectUnderTest.findAllByIsTaskOpenOrderByIdAsc(true)
        assertThat(tasks.size).isEqualTo(numberOfOpenTasksInTestDataSql)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then check for the number of closed tasks`() {
        val tasks: Set<Task> = objectUnderTest.findAllByIsTaskOpenOrderByIdAsc(false)
        assertThat(tasks.size).isEqualTo(numberOfClosedTasksInTestDataSql)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when all tasks are queried then check if the order is ascending by id`() {
        val tasks: Set<Task> = objectUnderTest.findAllByOrderByIdAsc()
        assertThat(tasks.elementAt(0).id).isEqualTo(111)
        assertThat(tasks.elementAt(1).id).isEqualTo(222)
        assertThat(tasks.elementAt(2).id).isEqualTo(333)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task created check then check if descriptions already exists`() {
        val isDescriptionAlreadyGiven = objectUnderTest.existsByDescription("test todo")

        assertThat(isDescriptionAlreadyGiven).isTrue
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task created check then check if descriptions does not exists`() {
        val isDescriptionAvailable = objectUnderTest.existsByDescription("feed the cat")

        assertThat(!isDescriptionAvailable).isTrue
    }
}
