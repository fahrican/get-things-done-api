package com.onecosys.get_things_done.util

import com.onecosys.get_things_done.data.entity.Task
import com.onecosys.get_things_done.data.model.dto.TaskDto
import com.onecosys.get_things_done.data.model.request.TaskCreateRequest

interface TaskMapper {

    fun toDto(entity: Task): TaskDto

    fun toEntity(request: TaskCreateRequest, entity: Task): Task
}