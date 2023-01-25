package com.onecosys.get_things_done.util

import com.onecosys.get_things_done.data.entity.Task
import com.onecosys.get_things_done.data.dto.TaskDto
import com.onecosys.get_things_done.data.request.TaskCreateRequest
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDateTime

@Component
class TaskMapperImpl : TaskMapper {
    override fun toDto(entity: Task) = TaskDto(
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

    override fun toEntity(request: TaskCreateRequest, clock: Clock, entity: Task): Task {
        entity.description = request.description
        entity.isReminderSet = request.isReminderSet
        entity.isTaskOpen = request.isTaskOpen
        entity.createdOn = LocalDateTime.now(clock)
        entity.finishedOn = request.finishedOn
        entity.timeInterval = request.timeInterval
        entity.timeTaken = request.timeTaken
        entity.priority = request.priority
        entity.startedOn = request.startedOn
        return entity
    }
}