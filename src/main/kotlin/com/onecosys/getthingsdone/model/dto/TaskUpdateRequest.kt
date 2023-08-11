package com.onecosys.getthingsdone.model.dto

import com.onecosys.getthingsdone.model.Priority
import java.time.LocalDateTime

data class TaskUpdateRequest(
    val description: String?,
    val isReminderSet: Boolean?,
    val isTaskOpen: Boolean?,
    val startedOn: LocalDateTime?,
    val finishedOn: LocalDateTime?,
    val timeInterval: String?,
    val timeTaken: Int?,
    val priority: Priority?
)
