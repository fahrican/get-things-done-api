package com.onecosys.get_things_done.model.request

import com.onecosys.get_things_done.model.entity.Priority
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class TaskCreateRequest(

        @Size(min = MIN_DESCRIPTION_LENGTH, max = MAX_DESCRIPTION_LENGTH)
        val description: String,

        val isReminderSet: Boolean,

        val isTaskOpen: Boolean,

        val startedOn: LocalDateTime?,

        val finishedOn: LocalDateTime?,

        @NotBlank(message = "time_interval can't be empty")
        val timeInterval: String,

        val timeTaken: Int?,

        val priority: Priority
)
const val MIN_DESCRIPTION_LENGTH: Int = 3
const val MAX_DESCRIPTION_LENGTH: Int = 255