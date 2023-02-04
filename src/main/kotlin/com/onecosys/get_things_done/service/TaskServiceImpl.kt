package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.error_handling.BadRequestException
import com.onecosys.get_things_done.error_handling.TaskNotFoundException
import com.onecosys.get_things_done.model.dto.TaskDto
import com.onecosys.get_things_done.model.entity.Task
import com.onecosys.get_things_done.model.request.*
import com.onecosys.get_things_done.repository.TaskRepository
import com.onecosys.get_things_done.util.TaskMapper
import com.onecosys.get_things_done.util.TaskTimestamp
import org.springframework.beans.BeanUtils
import org.springframework.beans.BeanWrapperImpl
import org.springframework.stereotype.Service
import java.beans.PropertyDescriptor

@Service
class TaskServiceImpl(
    private val repository: TaskRepository,
    private val mapper: TaskMapper,
    private val taskTimestamp: TaskTimestamp
) : TaskService {

    override fun getTasks(status: TaskStatus?): List<TaskDto> {
        return when (status) {
            TaskStatus.OPEN -> repository.findAllByIsTaskOpenOrderByIdAsc(true).map(mapper::toDto)
            TaskStatus.CLOSED -> repository.findAllByIsTaskOpenOrderByIdAsc(false).map(mapper::toDto)
            else -> repository.findAllByOrderByIdAsc().map(mapper::toDto)
        }
    }


    override fun getTaskById(id: Long): TaskDto {
        validateTaskIdExistence(id)
        val task: Task = repository.findTaskById(id)
        return mapper.toDto(task)
    }

    override fun createTask(createRequest: TaskCreateRequest): TaskDto {
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

    override fun updateTask(id: Long, updateRequest: TaskUpdateRequest): TaskDto {
        validateTaskIdExistence(id)
        val existingTask: Task = repository.findTaskById(id)

        BeanUtils.copyProperties(updateRequest, existingTask, *getNullProperties(updateRequest))
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

    private fun getNullProperties(sourceObject: Any): Array<String> {
        val sourceWrapper = BeanWrapperImpl(sourceObject)
        val propertyDescriptors: Array<PropertyDescriptor> = sourceWrapper.propertyDescriptors
        return propertyDescriptors.filter { property ->
            sourceWrapper.getPropertyValue(property.name) == null
        }.map { property -> property.name }.toTypedArray()
    }
}