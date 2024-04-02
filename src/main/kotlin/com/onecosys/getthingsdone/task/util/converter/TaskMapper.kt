package com.onecosys.getthingsdone.task.util.converter

import com.onecosys.getthingsdone.dto.TaskCreateRequest
import com.onecosys.getthingsdone.dto.TaskFetchResponse
import com.onecosys.getthingsdone.task.entity.Task
import com.onecosys.getthingsdone.user.entity.User
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.OffsetDateTime

@Component
class TaskMapper {

    fun toDto(entity: Task) = TaskFetchResponse(
        id = entity.id,
        description = entity.description,
        isReminderSet = entity.isReminderSet,
        isTaskOpen = entity.isTaskOpen,
        createdOn = entity.createdOn,
        startedOn = entity.startedOn,
        finishedOn = entity.finishedOn,
        timeInterval = entity.timeInterval,
        timeTaken = entity.timeTaken,
        priority = entity.priority
    )

    fun toEntity(request: TaskCreateRequest, clock: Clock, user: User): Task {
        val task = Task()
        task.description = request.description
        task.isReminderSet = request.isReminderSet
        task.isTaskOpen = request.isTaskOpen
        task.createdOn = OffsetDateTime.now(clock)
        task.finishedOn = request.finishedOn
        task.timeInterval = request.timeInterval
        task.timeTaken = request.timeTaken
        task.priority = request.priority
        task.startedOn = request.startedOn
        task.user = user
        return task
    }
}
