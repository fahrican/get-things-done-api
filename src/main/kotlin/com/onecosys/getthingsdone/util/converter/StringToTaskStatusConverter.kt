package com.onecosys.getthingsdone.util.converter

import com.onecosys.getthingsdone.errorhandling.BadRequestException
import com.onecosys.getthingsdone.model.request.TaskStatus
import org.springframework.core.convert.converter.Converter
import java.util.Locale

class StringToTaskStatusConverter : Converter<String, TaskStatus> {

    override fun convert(source: String): TaskStatus {
        if (source.isNotEmpty() && source.uppercase(Locale.getDefault()) != "OPEN" && source.uppercase(Locale.getDefault()) != "CLOSED") {
            throw BadRequestException("Query parameter 'status' can only be 'status=open' or 'status=closed'")
        }
        return TaskStatus.valueOf(source.uppercase(Locale.getDefault()))
    }
}
