package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.data.entity.Task
import com.onecosys.get_things_done.data.dto.TaskDto
import com.onecosys.get_things_done.data.request.MAX_DESCRIPTION_LENGTH
import com.onecosys.get_things_done.data.request.MIN_DESCRIPTION_LENGTH
import com.onecosys.get_things_done.data.request.TaskCreateRequest
import com.onecosys.get_things_done.data.request.TaskUpdateRequest
import com.onecosys.get_things_done.error_handling.BadRequestException
import com.onecosys.get_things_done.error_handling.TaskNotFoundException
import com.onecosys.get_things_done.repository.TaskRepository
import com.onecosys.get_things_done.util.TaskMapper
import com.onecosys.get_things_done.util.TaskTimestamp
import org.springframework.stereotype.Service
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Field
import java.util.stream.Collectors
import kotlin.reflect.full.memberProperties

@Service
class TaskServiceImpl(
    private val repository: TaskRepository,
    private val mapper: TaskMapper,
    private val taskTimestamp: TaskTimestamp
) : TaskService {

    override fun getAllTasks(): List<TaskDto> =
        repository.queryAllTasks().stream().map { entity -> mapper.toDto(entity) }.collect(Collectors.toList())

    override fun getAllOpenTasks(): List<TaskDto> =
        repository.queryAllOpenTasks().stream().map { entity -> mapper.toDto(entity) }.collect(Collectors.toList())

    override fun getAllClosedTasks(): List<TaskDto> =
        repository.queryAllClosedTasks().stream().map { entity -> mapper.toDto(entity) }.collect(Collectors.toList())

    override fun getTaskById(id: Long): TaskDto {
        checkForTaskId(id)
        val task: Task = repository.findTaskById(id)
        return mapper.toDto(task)
    }

    override fun createTask(createRequest: TaskCreateRequest): TaskDto {
        if (createRequest.description.length < MIN_DESCRIPTION_LENGTH || createRequest.description.length > MAX_DESCRIPTION_LENGTH) {
            throw BadRequestException(message = "Description needs to be at least $MIN_DESCRIPTION_LENGTH characters long or maximum $MAX_DESCRIPTION_LENGTH")
        }
        if (repository.doesDescriptionExist(createRequest.description)) {
            throw BadRequestException(message = "There is already a task with description: ${createRequest.description}")
        }
        val task = Task()
        mapper.toEntity(createRequest, taskTimestamp.createClockWithZone(), task)
        val savedTask = repository.save(task)
        return mapper.toDto(savedTask)
    }

    override fun updateTask(id: Long, updateRequest: TaskUpdateRequest): TaskDto {
        checkForTaskId(id)
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
        checkForTaskId(id)
        repository.deleteById(id)
        return "Task with id: $id has been deleted."
    }

    private fun checkForTaskId(id: Long) {
        if (!repository.existsById(id)) {
            throw TaskNotFoundException(message = "Task with ID: $id does not exist!")
        }
    }
}