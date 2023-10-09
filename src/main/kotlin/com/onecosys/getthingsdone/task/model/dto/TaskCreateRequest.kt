package com.onecosys.getthingsdone.task.model.dto

import com.onecosys.getthingsdone.task.model.Priority
import com.onecosys.getthingsdone.task.model.entity.MAX_DESCRIPTION_LENGTH
import com.onecosys.getthingsdone.task.model.entity.MIN_DESCRIPTION_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

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
