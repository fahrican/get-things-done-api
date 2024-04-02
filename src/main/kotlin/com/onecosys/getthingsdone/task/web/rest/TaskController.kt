package com.onecosys.getthingsdone.task.web.rest

import com.onecosys.getthingsdone.api.TaskResource
import com.onecosys.getthingsdone.authentication.service.UserSessionService
import com.onecosys.getthingsdone.dto.TaskCreateRequest
import com.onecosys.getthingsdone.dto.TaskFetchResponse
import com.onecosys.getthingsdone.dto.TaskStatus
import com.onecosys.getthingsdone.dto.TaskUpdateRequest
import com.onecosys.getthingsdone.task.service.TaskService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class TaskController(
    private val service: TaskService,
    private val userProvider: UserSessionService
) : TaskResource {

    override fun createTask(taskCreateRequest: TaskCreateRequest): ResponseEntity<TaskFetchResponse> {
        val task = service.createTask(taskCreateRequest, userProvider.getAuthenticatedUser())
        return ResponseEntity(task, HttpStatus.CREATED)
    }

    override fun deleteTask(id: Long): ResponseEntity<Unit> {
        val headerValue: String = service.deleteTask(id, userProvider.getAuthenticatedUser())
        val httpHeader = HttpHeaders()
        httpHeader.add("delete-task-header", headerValue)
        return ResponseEntity(null, httpHeader, HttpStatus.NO_CONTENT)

    }


    override fun getTaskById(id: Long) = ResponseEntity.ok(service.getTaskById(id, userProvider.getAuthenticatedUser()))

    override fun getTasks(status: TaskStatus?): ResponseEntity<List<TaskFetchResponse>> {
        val tasks = service.getTasks(userProvider.getAuthenticatedUser(), status).toList()
        return ResponseEntity(tasks, HttpStatus.OK)
    }

    override fun updateTask(
        id: Long,
        taskUpdateRequest: TaskUpdateRequest
    ) = ResponseEntity.ok(service.updateTask(id, taskUpdateRequest, userProvider.getAuthenticatedUser()))
}
