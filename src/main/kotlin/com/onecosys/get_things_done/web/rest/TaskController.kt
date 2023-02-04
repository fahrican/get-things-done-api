package com.onecosys.get_things_done.web.rest

import com.onecosys.get_things_done.model.dto.TaskDto
import com.onecosys.get_things_done.model.request.TaskCreateRequest
import com.onecosys.get_things_done.model.request.TaskStatus
import com.onecosys.get_things_done.model.request.TaskUpdateRequest
import com.onecosys.get_things_done.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("api/v1/tasks")
class TaskController(private val service: TaskService) {

    @GetMapping
    fun getTasks(
        @RequestParam("status", required = false) status: TaskStatus?
    ): ResponseEntity<List<TaskDto>> = ResponseEntity.ok(service.getTasks(status))

    @GetMapping("{id}")
    fun getTaskById(@PathVariable id: Long): ResponseEntity<TaskDto> = ResponseEntity.ok(service.getTaskById(id))

    @PostMapping
    fun createTask(@Valid @RequestBody createRequest: TaskCreateRequest): ResponseEntity<TaskDto> {
        val task = service.createTask(createRequest)
        return ResponseEntity(task, HttpStatus.CREATED)
    }

    @PatchMapping("{id}")
    fun updateTask(
        @PathVariable id: Long,
        @Valid @RequestBody updateRequest: TaskUpdateRequest
    ): ResponseEntity<TaskDto> = ResponseEntity.ok(service.updateTask(id, updateRequest))

    @DeleteMapping("{id}")
    fun deleteTaskWithId(@PathVariable id: Long): ResponseEntity<String> = ResponseEntity.ok(service.deleteTask(id))
}
