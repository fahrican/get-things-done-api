package com.onecosys.get_things_done.repository

import com.onecosys.get_things_done.entity.Task
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql

@DataJpaTest(properties = ["spring.jpa.properties.javax.persistence.validation.mode=none"])
@ActiveProfiles("test")
internal class TaskRepositoryTestEmbedded {

    @Autowired
    private lateinit var objectUnderTest: TaskRepository

    private val numberOfRecordsInTestDataSql = 3
    private val numberOfClosedTasksInTestDataSql = 2
    private val numberOfOpenTasksInTestDataSql = 1


    @Test
    fun `when task is saved then check if it is properly saved`() {
        val task = Task()
        val savedTask: Task = objectUnderTest.save(task)
        assertThat(savedTask).usingRecursiveComparison().ignoringFields("id").isEqualTo(task)
    }

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
        val tasks: List<Task> = objectUnderTest.findAll()
        objectUnderTest.deleteById(222)
        assertThat(tasks.size).isEqualTo(numberOfRecordsInTestDataSql)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then check for the number of open tasks`() {
        val tasks: List<Task> = objectUnderTest.queryAllOpenTasks()
        assertThat(tasks.size).isEqualTo(numberOfOpenTasksInTestDataSql)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task saved through SQL file then check for the number of closed tasks`() {
        val tasks: List<Task> = objectUnderTest.queryAllClosedTasks()
        assertThat(tasks.size).isEqualTo(numberOfClosedTasksInTestDataSql)
    }


    @Test
    @Disabled
    fun `when task is saved then check if description is not null and unique`() {
        // GIVEN
        val task1 = Task()
        task1.description = "test"
        val task2 = Task()
        task2.description = "test"
        objectUnderTest.save(task1)
        //objectUnderTest.save(task2)

        // WHEN
        // THEN
        AssertionsForClassTypes.assertThatThrownBy { objectUnderTest.save(task2) }
            .hasMessageContaining("could not execute statement; SQL [n/a]; constraint [\"PUBLIC.UK_NUXJDIQ9O90T2L66B8NYURQ4T_INDEX_2 ON PUBLIC.TASK(DESCRIPTION NULLS FIRST) VALUES ( /* 1 */ 'test' )\"; SQL statement:")
            .isInstanceOf(DataIntegrityViolationException::class.java)


        //assertThat(objectUnderTest.findTaskById(1L).description).isEqualTo(objectUnderTest.findTaskById(2L).description)
    }

}