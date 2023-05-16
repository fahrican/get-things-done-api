package com.onecosys.getthingsdone.service

import com.onecosys.getthingsdone.model.dto.TaskDto
import com.onecosys.getthingsdone.model.dto.TaskCreateDto
import com.onecosys.getthingsdone.model.TaskStatus
import com.onecosys.getthingsdone.model.dto.TaskUpdateDto

interface TaskService {

    fun getTasks(status: TaskStatus?): Set<TaskDto>

    fun getTaskById(id: Long): TaskDto

    fun createTask(createRequest: TaskCreateDto): TaskDto

    fun updateTask(id: Long, updateRequest: TaskUpdateDto): TaskDto

    fun deleteTask(id: Long): String
}
