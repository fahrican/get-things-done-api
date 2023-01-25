package com.onecosys.get_things_done.web.rest

import com.onecosys.get_things_done.data.dto.TaskDto
import com.onecosys.get_things_done.data.request.TaskCreateRequest
import com.onecosys.get_things_done.data.request.TaskUpdateRequest
import com.onecosys.get_things_done.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("api/v1/tasks")
class TaskResource(private val service: TaskService) {

    @GetMapping("all")
    fun getAllTasks(): ResponseEntity<List<TaskDto>> = ResponseEntity.ok(service.getAllTasks())

    @GetMapping("open")
    fun getAllOpenTasks(): ResponseEntity<List<TaskDto>> = ResponseEntity.ok(service.getAllOpenTasks())

    @GetMapping("closed")
    fun getAllFinishedTasks(): ResponseEntity<List<TaskDto>> = ResponseEntity.ok(service.getAllClosedTasks())

    @GetMapping("{id}")
    fun getTaskById(@PathVariable id: Long): ResponseEntity<TaskDto> = ResponseEntity.ok(service.getTaskById(id))

    @PostMapping
    fun createTask(
            @Valid @RequestBody createRequest: TaskCreateRequest
    ): ResponseEntity<TaskDto> = ResponseEntity(service.createTask(createRequest), HttpStatus.CREATED)

    @PatchMapping("{id}")
    fun updateTask(
            @PathVariable id: Long,
            @Valid @RequestBody updateRequest: TaskUpdateRequest
    ): ResponseEntity<TaskDto> = ResponseEntity.ok(service.updateTask(id, updateRequest))

    @DeleteMapping("{id}")
    fun deleteTaskWithUri(@PathVariable id: Long): ResponseEntity<String> = ResponseEntity.ok(service.deleteTask(id))

    @DeleteMapping
    fun deleteTaskWithParam(@RequestParam id: Long): ResponseEntity<String> = ResponseEntity.ok(service.deleteTask(id))
}