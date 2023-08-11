package com.onecosys.getthingsdone.service

import com.onecosys.getthingsdone.error.handling.BadRequestException
import com.onecosys.getthingsdone.error.handling.TaskNotFoundException
import com.onecosys.getthingsdone.model.TaskStatus
import com.onecosys.getthingsdone.model.dto.*
import com.onecosys.getthingsdone.model.entity.MAX_DESCRIPTION_LENGTH
import com.onecosys.getthingsdone.model.entity.MIN_DESCRIPTION_LENGTH
import com.onecosys.getthingsdone.model.entity.Task
import com.onecosys.getthingsdone.repository.TaskRepository
import com.onecosys.getthingsdone.util.TaskTimestamp
import com.onecosys.getthingsdone.util.converter.TaskMapper
import org.springframework.stereotype.Service
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Field
import kotlin.reflect.full.memberProperties

@Service
class TaskServiceImpl(
    private val repository: TaskRepository,
    private val mapper: TaskMapper,
    private val taskTimestamp: TaskTimestamp
) : TaskService {

    override fun getTasks(status: TaskStatus?): Set<TaskFetchResponse> {
        return when (status) {
            TaskStatus.OPEN -> repository.findAllByIsTaskOpenOrderByIdAsc(true).map(mapper::toDto).toSet()
            TaskStatus.CLOSED -> repository.findAllByIsTaskOpenOrderByIdAsc(false).map(mapper::toDto).toSet()
            else -> repository.findAllByOrderByIdAsc().map(mapper::toDto).toSet()
        }
    }

    override fun getTaskById(id: Long): TaskFetchResponse {
        validateTaskIdExistence(id)
        val task: Task = repository.findTaskById(id)
        return mapper.toDto(task)
    }

    override fun createTask(createRequest: TaskCreateRequest): TaskFetchResponse {
        val descriptionLength: Int = createRequest.description.length
        if (descriptionLength < MIN_DESCRIPTION_LENGTH || descriptionLength > MAX_DESCRIPTION_LENGTH) {
            throw BadRequestException("Description must be between $MIN_DESCRIPTION_LENGTH and $MAX_DESCRIPTION_LENGTH characters in length")
        }
        if (repository.existsByDescription(createRequest.description)) {
            throw BadRequestException("A task with the description '${createRequest.description}' already exists")
        }
        val task: Task = mapper.toEntity(createRequest, taskTimestamp.createClockWithZone())
        val savedTask: Task = repository.save(task)
        return mapper.toDto(savedTask)
    }

    override fun updateTask(id: Long, updateRequest: TaskUpdateRequest): TaskFetchResponse {
        validateTaskIdExistence(id)
        val existingTask: Task = repository.findTaskById(id)

        for (prop in TaskUpdateRequest::class.memberProperties) {
            if (prop.get(updateRequest) != null) {
                val field: Field? = ReflectionUtils.findField(Task::class.java, prop.name)
                field?.let {
                    it.isAccessible = true
                    ReflectionUtils.setField(it, existingTask, prop.get(updateRequest))
                }
            }
        }

        val savedTask: Task = repository.save(existingTask)
        return mapper.toDto(savedTask)
    }

    override fun deleteTask(id: Long): String {
        validateTaskIdExistence(id)
        repository.deleteById(id)
        return "Task with id: $id has been deleted."
    }

    private fun validateTaskIdExistence(id: Long) {
        if (!repository.existsById(id)) {
            throw TaskNotFoundException(message = "Task with ID: $id does not exist!")
        }
    }
}
