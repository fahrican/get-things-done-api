package com.onecosys.getthingsdone.task.util.converter

import com.onecosys.getthingsdone.task.error.handling.BadRequestException
import com.onecosys.getthingsdone.task.model.TaskStatus
import com.onecosys.getthingsdone.task.util.converter.StringToTaskStatusConverter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class StringToTaskStatusConverterTest {

    private val converter = StringToTaskStatusConverter()

    @Test
    fun `convert should return the corresponding TaskStatus value for a valid string`() {
        assertThat(converter.convert("OPEN")).isEqualTo(TaskStatus.OPEN)
        assertThat(converter.convert("CLOSED")).isEqualTo(TaskStatus.CLOSED)
    }

    @Test
    fun `convert should throw ConversionFailedException for an invalid string`() {
        val expectedMessage = "Query parameter 'status' can only be 'status=open' or 'status=closed'"

        val exception = assertThrows(BadRequestException::class.java) { converter.convert("invalid") }

        assertThat(exception.message).isEqualTo(expectedMessage)
    }
}
