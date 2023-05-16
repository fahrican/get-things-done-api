package com.onecosys.getthingsdone.model.dto

import com.onecosys.getthingsdone.model.Priority
import com.onecosys.getthingsdone.model.entity.MAX_DESCRIPTION_LENGTH
import com.onecosys.getthingsdone.model.entity.MIN_DESCRIPTION_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class TaskCreateDto(

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
