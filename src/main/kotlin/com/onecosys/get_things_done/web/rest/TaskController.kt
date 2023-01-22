package com.onecosys.get_things_done.web.rest

import com.onecosys.get_things_done.data.model.dto.TaskDto
import com.onecosys.get_things_done.data.model.request.TaskCreateRequest
import com.onecosys.get_things_done.data.model.request.TaskUpdateRequest
import com.onecosys.get_things_done.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("api")
class TaskController(private val service: TaskService) {

    @GetMapping("all-tasks")
    fun getAllTasks(): ResponseEntity<List<TaskDto>> = ResponseEntity.ok(service.getAllTasks())

    @GetMapping("open-tasks")
    fun getAllOpenTasks(): ResponseEntity<List<TaskDto>> = ResponseEntity.ok(service.getAllOpenTasks())

    @GetMapping("closed-tasks")
    fun getAllFinishedTasks(): ResponseEntity<List<TaskDto>> = ResponseEntity.ok(service.getAllClosedTasks())

    @GetMapping("task/{id}")
    fun getTaskById(@PathVariable id: Long): ResponseEntity<TaskDto> = ResponseEntity.ok(service.getTaskById(id))

    @PostMapping("create")
    fun createTask(
            @Valid @RequestBody createRequest: TaskCreateRequest
    ): ResponseEntity<TaskDto> = ResponseEntity(service.createTask(createRequest), HttpStatus.CREATED)

    @PatchMapping("update/{id}")
    fun updateTask(
            @PathVariable id: Long,
            @Valid @RequestBody updateRequest: TaskUpdateRequest
    ): ResponseEntity<TaskDto> = ResponseEntity.ok(service.updateTask(id, updateRequest))

    @DeleteMapping("delete/{id}")
    fun deleteTaskWithUri(@PathVariable id: Long): ResponseEntity<String> = ResponseEntity.ok(service.deleteTask(id))

    @DeleteMapping("delete")
    fun deleteTaskWithParam(@RequestParam id: Long): ResponseEntity<String> = ResponseEntity.ok(service.deleteTask(id))
}