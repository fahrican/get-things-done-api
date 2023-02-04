package com.onecosys.get_things_done.util.converter

import com.onecosys.get_things_done.model.request.TaskStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.core.convert.ConversionFailedException

class StringToEnumConverterTest {

    private val converter = StringToEnumConverter()

    @Test
    fun `convert should return the corresponding TaskStatus value for a valid string`() {
        assertEquals(TaskStatus.OPEN, converter.convert("OPEN"))
        assertEquals(TaskStatus.CLOSED, converter.convert("CLOSED"))
    }

    @Test
    fun `convert should throw ConversionFailedException for an invalid string`() {
        val expectedMessage =
            "Failed to convert from type [java.lang.String] to type [com.onecosys.get_things_done.model.request.TaskStatus] for value 'INVALID'; nested exception is java.lang.IllegalArgumentException: No enum constant com.onecosys.get_things_done.model.request.TaskStatus.INVALID"

        val exception = assertThrows(ConversionFailedException::class.java) {
            converter.convert("INVALID")
        }
        assertEquals(
            expectedMessage,
            exception.message
        )
    }
}
