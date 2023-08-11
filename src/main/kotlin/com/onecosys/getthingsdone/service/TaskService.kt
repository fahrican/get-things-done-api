package com.onecosys.getthingsdone.service

import com.onecosys.getthingsdone.model.dto.TaskFetchResponse
import com.onecosys.getthingsdone.model.dto.TaskCreateRequest
import com.onecosys.getthingsdone.model.TaskStatus
import com.onecosys.getthingsdone.model.dto.TaskUpdateRequest

interface TaskService {

    fun getTasks(status: TaskStatus?): Set<TaskFetchResponse>

    fun getTaskById(id: Long): TaskFetchResponse

    fun createTask(createRequest: TaskCreateRequest): TaskFetchResponse

    fun updateTask(id: Long, updateRequest: TaskUpdateRequest): TaskFetchResponse

    fun deleteTask(id: Long): String
}
