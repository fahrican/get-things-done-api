package com.onecosys.getthingsdone.task.service

import com.onecosys.getthingsdone.dto.TaskCreateRequest
import com.onecosys.getthingsdone.dto.TaskFetchResponse
import com.onecosys.getthingsdone.dto.TaskStatus
import com.onecosys.getthingsdone.dto.TaskUpdateRequest
import com.onecosys.getthingsdone.user.entity.AppUser

interface TaskService {

    fun getTasks(appUser: AppUser, status: TaskStatus?): Set<TaskFetchResponse>

    fun getTaskById(id: Long, appUser: AppUser): TaskFetchResponse

    fun createTask(createRequest: TaskCreateRequest, appUser: AppUser): TaskFetchResponse

    fun updateTask(id: Long, updateRequest: TaskUpdateRequest, appUser: AppUser): TaskFetchResponse

    fun deleteTask(id: Long, appUser: AppUser): String
}
