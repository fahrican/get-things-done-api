package com.onecosys.getthingsdone.task.util.converter

import com.onecosys.getthingsdone.dto.TaskStatus
import com.onecosys.getthingsdone.error.BadRequestException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class StringToTaskStatusConverterTest {

    private val converter = StringToTaskStatusConverter()

    @Test
    fun `convert should return the corresponding TaskStatus value for a valid string`() {
        assertThat(converter.convert("open")).isEqualTo(TaskStatus.open)
        assertThat(converter.convert("closed")).isEqualTo(TaskStatus.closed)
    }

    @Test
    fun `convert should throw ConversionFailedException for an invalid string`() {
        val expectedMessage = "Query parameter 'status' can only be 'open' or 'closed'"

        val exception = assertThrows(BadRequestException::class.java) { converter.convert("invalid") }

        assertThat(exception.message).isEqualTo(expectedMessage)
    }
}
