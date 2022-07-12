package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.model.dto.TaskDto
import com.onecosys.get_things_done.entity.Task
import com.onecosys.get_things_done.repository.TaskRepository
import com.onecosys.get_things_done.model.request.CreateTaskRequest
import com.onecosys.get_things_done.model.request.TaskRequest
import com.onecosys.get_things_done.model.request.UpdateTaskRequest
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

    fun createTask(taskRequest: CreateTaskRequest): Task {
        val task = Task()
        assignValuesToEntity(task, taskRequest)
        return repository.save(task)
    }

    fun getTaskById(id: Long): TaskDto {
        val task = repository.findTaskById(id)
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

    fun updateTask(taskRequest: UpdateTaskRequest?): TaskDto {
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

    private fun assignValuesToEntity(task: Task, tr: TaskRequest) {
        task.description = tr.description
        task.isReminderSet = tr.isReminderSet
        task.isTaskOpen = tr.isTaskOpen
        task.createdOn = tr.createdOn
        task.finishedOn = tr.finishedOn
        task.timeInterval = tr.timeInterval
        task.timeTaken = tr.timeTaken
        task.priority = tr.priority
    }

    fun deleteTask(id: Long): String {
        repository.deleteById(id)
        return "Task with id: $id has been deleted."
    }
}