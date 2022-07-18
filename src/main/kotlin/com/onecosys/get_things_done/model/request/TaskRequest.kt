package com.onecosys.get_things_done.model.request

import com.onecosys.get_things_done.model.Priority
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

data class TaskRequest(

    @NotBlank(message = "task id can't be empty")
    val id: Long,

    @NotBlank(message = "description can't be empty")
    val description: String,

    val isReminderSet: Boolean,

    val isTaskOpen: Boolean,

    @NotBlank(message = "created_on can't be empty")
    val createdOn: LocalDateTime,

    val startedOn: LocalDateTime?,

    val finishedOn: LocalDateTime?,

    @NotBlank(message = "time_interval can't be empty")
    val timeInterval: String,

    val timeTaken: Int?,

    val priority: Priority
)