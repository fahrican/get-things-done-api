package com.onecosys.getthingsdone.service

import com.onecosys.getthingsdone.model.dto.TaskDto
import com.onecosys.getthingsdone.model.request.TaskCreateRequest
import com.onecosys.getthingsdone.model.request.TaskStatus
import com.onecosys.getthingsdone.model.request.TaskUpdateRequest

interface TaskService {

    fun getTasks(status: TaskStatus?): Set<TaskDto>

    fun getTaskById(id: Long): TaskDto

    fun createTask(createRequest: TaskCreateRequest): TaskDto

    fun updateTask(id: Long, updateRequest: TaskUpdateRequest): TaskDto

    fun deleteTask(id: Long): String
}