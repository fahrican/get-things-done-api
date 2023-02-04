package com.onecosys.get_things_done.util.converter

import com.onecosys.get_things_done.model.request.TaskStatus
import org.springframework.core.convert.ConversionFailedException
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.Converter
import java.util.Locale

class StringToEnumConverter : Converter<String, TaskStatus> {

    override fun convert(source: String): TaskStatus {
        try {
            return TaskStatus.valueOf(source.uppercase(Locale.getDefault()))
        } catch (iae: IllegalArgumentException) {
            throw ConversionFailedException(
                TypeDescriptor.valueOf(String::class.java), TypeDescriptor.valueOf(TaskStatus::class.java), source, iae
            )
        }
    }
}