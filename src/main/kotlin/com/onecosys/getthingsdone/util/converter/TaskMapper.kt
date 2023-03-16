package com.onecosys.getthingsdone.util.converter

import com.onecosys.getthingsdone.model.dto.TaskDto
import com.onecosys.getthingsdone.model.entity.Task
import com.onecosys.getthingsdone.model.request.TaskCreateRequest
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDateTime

@Component
class TaskMapper {

    fun toDto(entity: Task) = TaskDto(
        entity.id,
        entity.description,
        entity.isReminderSet,
        entity.isTaskOpen,
        entity.createdOn,
        entity.startedOn,
        entity.finishedOn,
        entity.timeInterval,
        entity.timeTaken,
        entity.priority
    )

    fun toEntity(request: TaskCreateRequest, clock: Clock): Task {
        val task = Task()
        task.description = request.description
        task.isReminderSet = request.isReminderSet
        task.isTaskOpen = request.isTaskOpen
        task.createdOn = LocalDateTime.now(clock)
        task.finishedOn = request.finishedOn
        task.timeInterval = request.timeInterval
        task.timeTaken = request.timeTaken
        task.priority = request.priority
        task.startedOn = request.startedOn
        return task
    }
}
