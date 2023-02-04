package com.onecosys.get_things_done.util.converter

import com.onecosys.get_things_done.error_handling.BadRequestException
import com.onecosys.get_things_done.model.request.TaskStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class StringToEnumConverterTest {

    private val converter = StringToEnumConverter()

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
