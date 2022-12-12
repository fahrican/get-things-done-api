package com.onecosys.get_things_done.data.model.request

import com.onecosys.get_things_done.data.model.Priority
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