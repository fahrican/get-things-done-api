package com.onecosys.getthingsdone.task.service

import com.onecosys.getthingsdone.task.model.dto.TaskFetchResponse
import com.onecosys.getthingsdone.task.model.dto.TaskCreateRequest
import com.onecosys.getthingsdone.task.model.TaskStatus
import com.onecosys.getthingsdone.task.model.dto.TaskUpdateRequest
import com.onecosys.getthingsdone.user.entity.User

interface TaskService {

    fun getTasks(status: TaskStatus?): Set<TaskFetchResponse>

    fun getTaskById(id: Long): TaskFetchResponse

    fun createTask(createRequest: TaskCreateRequest, user: User): TaskFetchResponse

    fun updateTask(id: Long, updateRequest: TaskUpdateRequest): TaskFetchResponse

    fun deleteTask(id: Long): String
}
