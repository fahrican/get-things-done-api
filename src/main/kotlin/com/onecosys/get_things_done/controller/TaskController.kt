package com.onecosys.get_things_done.controller

import com.onecosys.get_things_done.dto.TaskDto
import com.onecosys.get_things_done.service.TaskService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TaskController(private val service: TaskService) {

    @GetMapping("/tasks")
    fun getAllTasks(): List<TaskDto> = service.getAllTasks()

}