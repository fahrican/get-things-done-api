package com.onecosys.getthingsdone.task.util.converter

import com.onecosys.getthingsdone.error.BadRequestException
import com.onecosys.getthingsdone.models.TaskStatus
import org.springframework.core.convert.converter.Converter
import java.util.Locale

class StringToTaskStatusConverter : Converter<String, TaskStatus> {

    override fun convert(source: String): TaskStatus {
        return when (source.uppercase(Locale.getDefault())) {
            "OPEN" -> TaskStatus.valueOf("oPEN")
            "CLOSED" -> TaskStatus.valueOf("cLOSED")
            else -> throw BadRequestException("Query parameter 'status' can only be 'OPEN' or 'CLOSED'")
        }
    }
}
