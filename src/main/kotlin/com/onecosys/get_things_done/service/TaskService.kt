package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.model.dto.TaskDto
import com.onecosys.get_things_done.entity.Task
import com.onecosys.get_things_done.exception.BadRequestException
import com.onecosys.get_things_done.exception.TaskNotFoundException
import com.onecosys.get_things_done.repository.TaskRepository
import com.onecosys.get_things_done.model.request.TaskRequest
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class TaskService(private val repository: TaskRepository) {

    fun getAllTasks(): List<TaskDto> =
        repository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList())

    fun getAllOpenTasks(): List<TaskDto> =
        repository.queryAllOpenTasks().stream().map(this::convertEntityToDto).collect(Collectors.toList())

    fun getAllClosedTasks(): List<TaskDto> =
        repository.queryAllClosedTasks().stream().map(this::convertEntityToDto).collect(Collectors.toList())

    private fun convertEntityToDto(task: Task): TaskDto {
        return TaskDto(
            task.id,
            task.description,
            task.isReminderSet,
            task.isTaskOpen,
            task.createdOn,
            task.startedOn,
            task.finishedOn,
            task.timeInterval,
            task.timeTaken,
            task.priority
        )
    }

    fun createTask(taskRequest: TaskRequest): Task {
        if (repository.doesDescriptionExist(taskRequest.description)) {
            throw BadRequestException("There is already a task with description: ${taskRequest.description}")
        }
        val task = Task()
        assignValuesToEntity(task, taskRequest)
        return repository.save(task)
    }

    fun getTaskById(id: Long): TaskDto {
        if (!repository.existsById(id)) {
            throw TaskNotFoundException("Task with ID: $id does not exist!")
        }
        val task: Task = repository.findTaskById(id)
        return TaskDto(
            task.id,
            task.description,
            task.isReminderSet,
            task.isTaskOpen,
            task.createdOn,
            task.startedOn,
            task.finishedOn,
            task.timeInterval,
            task.timeTaken,
            task.priority
        )
    }

    fun updateTask(taskRequest: TaskRequest?): TaskDto {
        var savedTask = Task()
        taskRequest?.let { tr ->
            val task: Task = repository.findTaskById(tr.id)
            if (tr.description.isNotEmpty()) {
                assignValuesToEntity(task, tr)
            }
            savedTask = repository.save(task)
        }
        return TaskDto(
            savedTask.id,
            savedTask.description,
            savedTask.isReminderSet,
            savedTask.isTaskOpen,
            savedTask.createdOn,
            savedTask.startedOn,
            savedTask.finishedOn,
            savedTask.timeInterval,
            savedTask.timeTaken,
            savedTask.priority
        )
    }

    fun updateTask(id: Long, taskRequest: TaskRequest?): TaskDto {
        if (!repository.existsById(id)) {
            throw TaskNotFoundException("Task with ID: $id does not exist!")
        }
        val task: Task = repository.findTaskById(id)
        taskRequest?.let { tr ->
            if (tr.description.isNotEmpty()) {
                assignValuesToEntity(task, tr)
            }
        }
        val savedTask: Task = repository.save(task)
        return TaskDto(
            id,
            savedTask.description,
            savedTask.isReminderSet,
            savedTask.isTaskOpen,
            savedTask.createdOn,
            savedTask.startedOn,
            savedTask.finishedOn,
            savedTask.timeInterval,
            savedTask.timeTaken,
            savedTask.priority
        )
    }

    private fun assignValuesToEntity(task: Task, tr: TaskRequest) {
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

    fun deleteTask(id: Long): String {
        if (!repository.existsById(id)) {
            throw TaskNotFoundException("Task with ID: $id does not exist!")
        }
        repository.deleteById(id)
        return "Task with id: $id has been deleted."
    }
}