package com.onecosys.getthingsdone.task.service

import com.onecosys.getthingsdone.dto.TaskCreateRequest
import com.onecosys.getthingsdone.dto.TaskFetchResponse
import com.onecosys.getthingsdone.dto.TaskStatus
import com.onecosys.getthingsdone.dto.TaskUpdateRequest
import com.onecosys.getthingsdone.error.BadRequestException
import com.onecosys.getthingsdone.error.TaskNotFoundException
import com.onecosys.getthingsdone.task.entity.MAX_DESCRIPTION_LENGTH
import com.onecosys.getthingsdone.task.entity.MIN_DESCRIPTION_LENGTH
import com.onecosys.getthingsdone.task.entity.Task
import com.onecosys.getthingsdone.task.repository.TaskRepository
import com.onecosys.getthingsdone.task.util.TaskTimestamp
import com.onecosys.getthingsdone.task.util.converter.TaskMapper
import com.onecosys.getthingsdone.user.entity.AppUser
import org.springframework.stereotype.Service


@Service
class TaskServiceImpl(
    private val repository: TaskRepository,
    private val mapper: TaskMapper,
    private val taskTimestamp: TaskTimestamp
) : TaskService {

    override fun getTasks(appUser: AppUser, status: TaskStatus?): Set<TaskFetchResponse> {
        return when (status) {
            TaskStatus.open -> repository.findAllByAppUserAndIsTaskOpenOrderByIdAsc(appUser, true).map(mapper::toDto)
                .toSet()

            TaskStatus.closed -> repository.findAllByAppUserAndIsTaskOpenOrderByIdAsc(appUser, false).map(mapper::toDto)
                .toSet()

            else -> repository.findAllByAppUserOrderByIdAsc(appUser)
                .map(mapper::toDto)
                .toSet()
        }
    }

    override fun getTaskById(id: Long, appUser: AppUser): TaskFetchResponse {
        validateTaskIdExistence(id)
        val task: Task = repository.findTaskByIdAndAppUser(id, appUser)
        return mapper.toDto(task)
    }

    override fun createTask(createRequest: TaskCreateRequest, appUser: AppUser): TaskFetchResponse {
        val descriptionLength: Int = createRequest.description.length
        if (descriptionLength < MIN_DESCRIPTION_LENGTH || descriptionLength > MAX_DESCRIPTION_LENGTH) {
            throw BadRequestException("Description must be between $MIN_DESCRIPTION_LENGTH and $MAX_DESCRIPTION_LENGTH characters in length")
        }
        if (repository.existsByDescription(createRequest.description)) {
            throw BadRequestException("A task with the description '${createRequest.description}' already exists")
        }
        val task: Task = mapper.toEntity(createRequest, taskTimestamp.createClockWithZone(), appUser)
        val savedTask: Task = repository.save(task)
        return mapper.toDto(savedTask)
    }

    override fun updateTask(id: Long, updateRequest: TaskUpdateRequest, appUser: AppUser): TaskFetchResponse {
        validateTaskIdExistence(id)
        val existingTask: Task = repository.findTaskByIdAndAppUser(id, appUser)

        existingTask.apply {
            description = updateRequest.description ?: description
            isReminderSet = updateRequest.isReminderSet ?: isReminderSet
            isTaskOpen = updateRequest.isTaskOpen ?: isTaskOpen
            startedOn = updateRequest.startedOn ?: startedOn
            finishedOn = updateRequest.finishedOn ?: finishedOn
            timeInterval = updateRequest.timeInterval ?: timeInterval
            timeTaken = updateRequest.timeTaken ?: timeTaken
            priority = updateRequest.priority ?: priority
        }

        val savedTask: Task = repository.save(existingTask)
        return mapper.toDto(savedTask)
    }

    override fun deleteTask(id: Long, appUser: AppUser) {
        validateTaskIdExistence(id)
        repository.deleteById(id)
    }

    private fun validateTaskIdExistence(id: Long) {
        if (!repository.existsById(id)) {
            throw TaskNotFoundException(message = "Task with ID: $id does not exist!")
        }
    }
}
