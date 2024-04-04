package com.onecosys.getthingsdone.task.util.converter

import com.onecosys.getthingsdone.dto.TaskStatus
import com.onecosys.getthingsdone.error.BadRequestException
import org.springframework.core.convert.converter.Converter
import java.util.Locale

class StringToTaskStatusConverter : Converter<String, TaskStatus> {

    override fun convert(source: String): TaskStatus {
        return when (source.lowercase(Locale.getDefault())) {
            "open" -> TaskStatus.open
            "closed" -> TaskStatus.closed
            else -> throw BadRequestException("Query parameter 'status' can only be 'open' or 'closed'")
        }
    }
}
