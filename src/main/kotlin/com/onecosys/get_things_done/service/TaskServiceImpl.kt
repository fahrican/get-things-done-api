package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.error_handling.BadRequestException
import com.onecosys.get_things_done.error_handling.TaskNotFoundException
import com.onecosys.get_things_done.model.dto.TaskDto
import com.onecosys.get_things_done.model.entity.Task
import com.onecosys.get_things_done.model.request.TaskStatus
import com.onecosys.get_things_done.model.request.TaskCreateRequest
import com.onecosys.get_things_done.model.request.TaskUpdateRequest
import com.onecosys.get_things_done.model.request.MIN_DESCRIPTION_LENGTH
import com.onecosys.get_things_done.model.request.MAX_DESCRIPTION_LENGTH
import com.onecosys.get_things_done.repository.TaskRepository
import com.onecosys.get_things_done.util.TaskMapper
import com.onecosys.get_things_done.util.TaskTimestamp
import org.springframework.beans.BeanUtils
import org.springframework.beans.BeanWrapperImpl
import org.springframework.stereotype.Service
import java.beans.PropertyDescriptor
import java.util.Locale

@Service
class TaskServiceImpl(
    private val repository: TaskRepository,
    private val mapper: TaskMapper,
    private val taskTimestamp: TaskTimestamp
) : TaskService {

    companion object {
        private const val TASK_STATUS_OPEN = "open"
        private const val TASK_STATUS_CLOSED = "closed"
    }

    override fun getTasks(status: String?): List<TaskDto> {
        validateTaskStatus(status)
        return when (status) {
            TASK_STATUS_OPEN -> repository.findAllByIsTaskOpenOrderByIdAsc(true).map(mapper::toDto)

            TASK_STATUS_CLOSED -> repository.findAllByIsTaskOpenOrderByIdAsc(false).map(mapper::toDto)

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

    private fun validateTaskStatus(status: String?) {
        status?.let {
            try {
                TaskStatus.valueOf(status.uppercase(Locale.getDefault()))
            } catch (e: IllegalArgumentException) {
                throw BadRequestException("Query parameter 'status' can only be 'status=open' or 'status=closed'")
            }
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