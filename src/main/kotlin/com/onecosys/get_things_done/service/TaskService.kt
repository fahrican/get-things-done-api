package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.dto.TaskDto
import com.onecosys.get_things_done.model.Task
import com.onecosys.get_things_done.repository.TaskRepository
import com.onecosys.get_things_done.request.CreateTaskRequest
import com.onecosys.get_things_done.request.UpdateTaskRequest
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class TaskService(private val repository: TaskRepository) {

    fun getAllTasks(): List<TaskDto> =
        repository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList())

    private fun convertEntityToDto(task: Task): TaskDto {
        return TaskDto(
            task.taskId,
            task.description,
            task.isReminderSet,
            task.isTaskOpen,
            task.createdOn,
            task.startedOn,
            task.finishedOn,
            task.timeInterval,
            task.timeTaken
        )
    }

    fun createTask(taskRequest: CreateTaskRequest): Task {
        val task = Task()
        task.description = taskRequest.description
        task.isReminderSet = taskRequest.isReminderSet
        task.isTaskOpen = taskRequest.isTaskOpen
        task.createdOn = taskRequest.createdOn
        task.startedOn = taskRequest.startedOn
        task.finishedOn = taskRequest.finishedOn
        task.timeTaken = taskRequest.timeTaken
        return repository.save(task)
    }

    fun getTaskById(id: Long): TaskDto {
        val task = repository.findTaskById(id)
        return TaskDto(
            task.taskId,
            task.description,
            task.isReminderSet,
            task.isTaskOpen,
            task.createdOn,
            task.startedOn,
            task.finishedOn,
            task.timeInterval,
            task.timeTaken
        )
    }

    fun updateTask(taskRequest: UpdateTaskRequest?): TaskDto {
        var savedTask = Task()
        taskRequest?.let { tr ->
            val task: Task = repository.findTaskById(tr.id)
            if (tr.description.isNotEmpty()) {
                task.description = tr.description
                task.isReminderSet = tr.isReminderSet
                task.isTaskOpen = tr.isTaskOpen
                task.createdOn = tr.createdOn
                task.finishedOn = tr.finishedOn
                task.timeInterval = tr.timeInterval
                task.timeTaken = tr.timeTaken
            }
            savedTask = repository.save(task)
        }
        return TaskDto(
            savedTask.taskId,
            savedTask.description,
            savedTask.isReminderSet,
            savedTask.isTaskOpen,
            savedTask.createdOn,
            savedTask.startedOn,
            savedTask.finishedOn,
            savedTask.timeInterval,
            savedTask.timeTaken
        )
    }

    fun deleteTask(id: Long): String {
        repository.deleteById(id)
        return "Task with id: $id has been deleted."
    }
}