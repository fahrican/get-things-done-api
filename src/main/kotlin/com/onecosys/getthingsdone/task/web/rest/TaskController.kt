package com.onecosys.getthingsdone.task.web.rest

import com.onecosys.getthingsdone.apis.TaskResource
import com.onecosys.getthingsdone.authentication.service.UserAuthService
import com.onecosys.getthingsdone.models.TaskCreateRequest
import com.onecosys.getthingsdone.models.TaskFetchResponse
import com.onecosys.getthingsdone.models.TaskStatus
import com.onecosys.getthingsdone.models.TaskUpdateRequest
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
    private val userProvider: UserAuthService
) : TaskResource {

    override fun createTask(taskCreateRequest: TaskCreateRequest): ResponseEntity<TaskFetchResponse> {
        val task = service.createTask(taskCreateRequest, userProvider.getUser())
        return ResponseEntity(task, HttpStatus.CREATED)
    }

    override fun deleteTask(id: Long): ResponseEntity<Unit> {
        val headerValue: String = service.deleteTask(id, userProvider.getUser())
        val httpHeader = HttpHeaders()
        httpHeader.add("delete-task-header", headerValue)
        return ResponseEntity(null, httpHeader, HttpStatus.NO_CONTENT)

    }


    override fun getTaskById(id: Long) = ResponseEntity.ok(service.getTaskById(id, userProvider.getUser()))

    override fun getTasks(status: TaskStatus?): ResponseEntity<List<TaskFetchResponse>> {
        val tasks = service.getTasks(userProvider.getUser(), status).toList()
        return ResponseEntity(tasks, HttpStatus.OK)
    }

    override fun updateTask(
        id: Long,
        taskUpdateRequest: TaskUpdateRequest
    ) = ResponseEntity.ok(service.updateTask(id, taskUpdateRequest, userProvider.getUser()))
}
