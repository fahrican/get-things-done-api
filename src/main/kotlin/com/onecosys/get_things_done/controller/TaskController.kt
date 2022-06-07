package com.onecosys.get_things_done.controller

import com.onecosys.get_things_done.dto.TaskDto
import com.onecosys.get_things_done.request.CreateTaskRequest
import com.onecosys.get_things_done.service.TaskService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


@RestController
class TaskController(private val service: TaskService) {

    @GetMapping("/tasks")
    fun getAllTasks(): List<TaskDto> = service.getAllTasks()


    @GetMapping("task/{id}")
    fun getTaskById(@PathVariable id: Long): TaskDto {
        return service.getTaskById(id)
    }

    @PostMapping("create")
    fun createStudent(@Valid @RequestBody taskRequest: CreateTaskRequest): TaskDto {
        val task = service.createTask(taskRequest)
        return TaskDto(task.taskId, task.description, task.isReminderSet, task.isTaskOpen, task.createdOn, task.startedOn, task.finishedOn, task.timeInterval, task.timeTaken)
    }
}