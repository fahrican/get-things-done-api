package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.dto.TaskDto
import com.onecosys.get_things_done.model.Task
import com.onecosys.get_things_done.repository.TaskRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class TaskServiceTest {

    @Mock
    private lateinit var repository: TaskRepository

    private lateinit var objectUnderTest: TaskService

    @BeforeEach
    fun setUp() {
        objectUnderTest = TaskService(repository)
    }

    @AfterEach
    fun tearDown() {
        // to be implemented
    }

    @Test
    fun `when all tasks get fetched then check if the given size is correct`() {
        val expectedTasks = listOf(Task(), Task())
        Mockito.`when`(repository.findAll()).thenReturn(expectedTasks.toMutableList())

        val actualList: List<TaskDto> = objectUnderTest.getAllTasks()
        Assertions.assertThat(actualList.size).isEqualTo(expectedTasks.size)
    }
}