package com.onecosys.get_things_done.service

import com.onecosys.get_things_done.data.dto.TaskDto
import com.onecosys.get_things_done.data.request.TaskCreateRequest
import com.onecosys.get_things_done.data.request.TaskUpdateRequest

interface TaskService {

    fun getAllTasks(): List<TaskDto>

    fun getAllOpenTasks(): List<TaskDto>

    fun getAllClosedTasks(): List<TaskDto>

    fun getTaskById(id: Long): TaskDto

    fun createTask(createRequest: TaskCreateRequest): TaskDto

    fun updateTask(id: Long, updateRequest: TaskUpdateRequest): TaskDto

    fun deleteTask(id: Long): String
}