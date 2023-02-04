package com.onecosys.get_things_done.util.converter

import com.onecosys.get_things_done.error_handling.BadRequestException
import com.onecosys.get_things_done.model.request.TaskStatus
import org.springframework.core.convert.converter.Converter
import java.util.Locale

class StringToEnumConverter : Converter<String, TaskStatus> {

    override fun convert(source: String): TaskStatus {
        if (source.isNotEmpty() && source.uppercase(Locale.getDefault()) != "OPEN" && source.uppercase(Locale.getDefault()) != "CLOSED") {
            throw BadRequestException("Query parameter 'status' can only be 'status=open' or 'status=closed'")
        }
        return TaskStatus.valueOf(source.uppercase(Locale.getDefault()))
    }
}