package com.onecosys.getthingsdone.task.service

import com.onecosys.getthingsdone.models.TaskCreateRequest
import com.onecosys.getthingsdone.models.TaskFetchResponse
import com.onecosys.getthingsdone.models.TaskStatus
import com.onecosys.getthingsdone.models.TaskUpdateRequest
import com.onecosys.getthingsdone.user.entity.User

interface TaskService {

    fun getTasks(user: User, status: TaskStatus?): Set<TaskFetchResponse>

    fun getTaskById(id: Long, user: User): TaskFetchResponse

    fun createTask(createRequest: TaskCreateRequest, user: User): TaskFetchResponse

    fun updateTask(id: Long, updateRequest: TaskUpdateRequest, user: User): TaskFetchResponse

    fun deleteTask(id: Long, user: User): String
}
