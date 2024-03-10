package com.onecosys.getthingsdone.task.util.converter

import com.onecosys.getthingsdone.task.model.dto.TaskFetchResponse
import com.onecosys.getthingsdone.task.model.entity.Task
import com.onecosys.getthingsdone.task.model.dto.TaskCreateRequest
import com.onecosys.getthingsdone.user.entity.User
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDateTime

@Component
class TaskMapper {

    fun toDto(entity: Task) = TaskFetchResponse(
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

    fun toEntity(request: TaskCreateRequest, clock: Clock, user: User): Task {
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
        task.user = user
        return task
    }
}
