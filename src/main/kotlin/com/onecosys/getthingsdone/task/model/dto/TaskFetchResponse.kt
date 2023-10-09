package com.onecosys.getthingsdone.task.model.dto

import com.onecosys.getthingsdone.task.model.Priority
import java.time.LocalDateTime

data class TaskFetchResponse(
    val id: Long?,
    val description: String,
    val isReminderSet: Boolean,
    val isTaskOpen: Boolean,
    val createdOn: LocalDateTime,
    val startedOn: LocalDateTime?,
    val finishedOn: LocalDateTime?,
    val timeInterval: String,
    val timeTaken: Int?,
    val priority: Priority
)
