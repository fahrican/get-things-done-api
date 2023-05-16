package com.onecosys.getthingsdone.service

import com.onecosys.getthingsdone.model.dto.TaskFetchDto
import com.onecosys.getthingsdone.model.dto.TaskCreateDto
import com.onecosys.getthingsdone.model.TaskStatus
import com.onecosys.getthingsdone.model.dto.TaskUpdateDto

interface TaskService {

    fun getTasks(status: TaskStatus?): Set<TaskFetchDto>

    fun getTaskById(id: Long): TaskFetchDto

    fun createTask(createRequest: TaskCreateDto): TaskFetchDto

    fun updateTask(id: Long, updateRequest: TaskUpdateDto): TaskFetchDto

    fun deleteTask(id: Long): String
}
