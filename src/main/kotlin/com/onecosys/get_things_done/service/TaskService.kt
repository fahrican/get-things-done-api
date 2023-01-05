package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.data.model.dto.TaskDto
import com.onecosys.get_things_done.data.entity.Task
import com.onecosys.get_things_done.data.model.request.TaskCreateRequest
import com.onecosys.get_things_done.data.model.request.TaskUpdateRequest
import com.onecosys.get_things_done.exception.BadRequestException
import com.onecosys.get_things_done.exception.TaskNotFoundException
import com.onecosys.get_things_done.helper.toDto
import com.onecosys.get_things_done.repository.TaskRepository
import org.springframework.stereotype.Service
import java.util.stream.Collectors
import kotlin.reflect.full.memberProperties
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Field


@Service
class TaskService(private val repository: TaskRepository) {

    private fun assignValuesToEntity(task: Task, tr: TaskCreateRequest) {
        task.description = tr.description
        task.isReminderSet = tr.isReminderSet
        task.isTaskOpen = tr.isTaskOpen
        task.createdOn = tr.createdOn
        task.finishedOn = tr.finishedOn
        task.timeInterval = tr.timeInterval
        task.timeTaken = tr.timeTaken
        task.priority = tr.priority
        task.startedOn = tr.startedOn
    }

    private fun checkForTaskId(id: Long) {
        if (!repository.existsById(id)) {
            throw TaskNotFoundException("Task with ID: $id does not exist!")
        }
    }

    fun getAllTasks(): List<TaskDto> = repository.findAll().stream().map { it.toDto() }.collect(Collectors.toList())

    fun getAllOpenTasks(): List<TaskDto> = repository.queryAllOpenTasks().stream().map { it.toDto() }.collect(Collectors.toList())

    fun getAllClosedTasks(): List<TaskDto> = repository.queryAllClosedTasks().stream().map { it.toDto() }.collect(Collectors.toList())

    fun getTaskById(id: Long): TaskDto {
        checkForTaskId(id)
        val task: Task = repository.findTaskById(id)
        return task.toDto()
    }

    fun createTask(createRequest: TaskCreateRequest): TaskDto {
        if (repository.doesDescriptionExist(createRequest.description)) {
            throw BadRequestException("There is already a task with description: ${createRequest.description}")
        }
        val task = Task()
        assignValuesToEntity(task, createRequest)
        val savedTask = repository.save(task)
        return savedTask.toDto()
    }

    fun updateTask(id: Long, updateRequest: TaskUpdateRequest): TaskDto {
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
        return savedTask.toDto()
    }

    fun deleteTask(id: Long): String {
        checkForTaskId(id)
        repository.deleteById(id)
        return "Task with id: $id has been deleted."
    }
}