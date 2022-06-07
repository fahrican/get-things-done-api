package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.dto.TaskDto
import com.onecosys.get_things_done.model.Task
import com.onecosys.get_things_done.repository.TaskRepository
import com.onecosys.get_things_done.request.CreateTaskRequest
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class TaskService(private val repository: TaskRepository) {


    fun getAllTasks(): List<TaskDto> = repository.findAll().stream().map(this::convertEntityToDto).collect(Collectors.toList())


    private fun convertEntityToDto(task: Task): TaskDto {
        return TaskDto(task.taskId, task.description, task.isReminderSet, task.isTaskOpen, task.createdOn, task.startedOn, task.finishedOn, task.timeInterval, task.timeTaken)
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
        return TaskDto(task.taskId, task.description, task.isReminderSet, task.isTaskOpen, task.createdOn, task.startedOn, task.finishedOn, task.timeInterval, task.timeTaken)
    }
}