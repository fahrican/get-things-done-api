package com.onecosys.getthingsdone.task.web.rest

import com.onecosys.getthingsdone.task.model.dto.TaskFetchResponse
import com.onecosys.getthingsdone.task.model.dto.TaskCreateRequest
import com.onecosys.getthingsdone.task.model.TaskStatus
import com.onecosys.getthingsdone.task.model.dto.TaskUpdateRequest
import com.onecosys.getthingsdone.task.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
@RequestMapping("api/v1/tasks")
class TaskController(private val service: TaskService) {

    @Operation(summary = "Get all tasks", tags = ["task"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "all open & closed tasks", content = [
                    (Content(
                        mediaType = "application/json",
                        array = (ArraySchema(schema = Schema(implementation = TaskFetchResponse::class)))
                    ))]
            ),
            ApiResponse(responseCode = "400", description = "Bad Request", content = [Content()])]
    )
    @GetMapping
    fun getTasks(
        @RequestParam("status", required = false) status: TaskStatus?
    ): ResponseEntity<Set<TaskFetchResponse>> = ResponseEntity.ok(service.getTasks(status))

    @Operation(summary = "Get task by its ID", tags = ["task"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Found the task by the supplied ID",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = TaskFetchResponse::class)
                )]
            ),
            ApiResponse(responseCode = "404", description = "Task not found", content = [Content()]),
            ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = [Content()])
        ]
    )
    @GetMapping("{id}")
    fun getTaskById(@PathVariable id: Long): ResponseEntity<TaskFetchResponse> =
        ResponseEntity.ok(service.getTaskById(id))

    @Operation(summary = "Create a new task", tags = ["task"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Task successfully created",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = TaskFetchResponse::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()]),
        ]
    )
    @PostMapping
    fun createTask(
        @Valid @RequestBody
        createRequest: TaskCreateRequest
    ): ResponseEntity<TaskFetchResponse> {
        val task = service.createTask(createRequest)
        return ResponseEntity(task, HttpStatus.CREATED)
    }

    @Operation(summary = "Update an existing task", tags = ["task"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Task successfully updated",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = TaskFetchResponse::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Task not found", content = [Content()])
        ]
    )
    @PatchMapping("{id}")
    fun updateTask(
        @PathVariable id: Long,
        @Valid @RequestBody
        updateRequest: TaskUpdateRequest
    ): ResponseEntity<TaskFetchResponse> = ResponseEntity.ok(service.updateTask(id, updateRequest))

    @Operation(summary = "Delete a task by its ID", tags = ["task"])
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Task successfully deleted", content = [Content()]),
            ApiResponse(responseCode = "400", description = "Invalid ID supplied", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Task not found", content = [Content()])
        ]
    )
    @DeleteMapping("{id}")
    fun deleteTask(@PathVariable id: Long): ResponseEntity<Unit> {
        val headerValue: String = service.deleteTask(id)
        val httpHeader = HttpHeaders()
        httpHeader.add("delete-task-header", headerValue)
        return ResponseEntity(null, httpHeader, HttpStatus.NO_CONTENT)
    }
}
