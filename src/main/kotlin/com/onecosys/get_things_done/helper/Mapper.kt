package com.onecosys.get_things_done.helper

import com.onecosys.get_things_done.data.entity.Task
import com.onecosys.get_things_done.data.model.dto.TaskDto
import com.onecosys.get_things_done.data.model.request.TaskCreateRequest

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

fun Task.toEntity(request: TaskCreateRequest) {
    this.description = request.description
    this.isReminderSet = request.isReminderSet
    this.isTaskOpen = request.isTaskOpen
    this.createdOn = request.createdOn
    this.finishedOn = request.finishedOn
    this.timeInterval = request.timeInterval
    this.timeTaken = request.timeTaken
    this.priority = request.priority
    this.startedOn = request.startedOn
}