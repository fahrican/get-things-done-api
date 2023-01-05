package com.onecosys.get_things_done.helper

import com.onecosys.get_things_done.data.entity.Task
import com.onecosys.get_things_done.data.model.dto.TaskDto

fun Task.toDto() = TaskDto(
        this.id,
        this.description,
        this.isReminderSet,
        this.isTaskOpen,
        this.createdOn,
        this.startedOn,
        this.finishedOn,
        this.timeInterval,
        this.timeTaken,
        this.priority
)

