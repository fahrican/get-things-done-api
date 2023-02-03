package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.model.dto.TaskDto
import com.onecosys.get_things_done.model.request.TaskCreateRequest
import com.onecosys.get_things_done.model.request.TaskUpdateRequest

interface TaskService {

    fun getTasks(status: String?): List<TaskDto>

    fun getTaskById(id: Long): TaskDto

    fun createTask(createRequest: TaskCreateRequest): TaskDto

    fun updateTask(id: Long, updateRequest: TaskUpdateRequest): TaskDto

    fun deleteTask(id: Long): String
}