package com.onecosys.get_things_done.util

import com.onecosys.get_things_done.data.entity.Task
import com.onecosys.get_things_done.data.model.dto.TaskDto
import com.onecosys.get_things_done.data.model.request.TaskCreateRequest
import java.time.Clock

interface TaskMapper {

    fun toDto(entity: Task): TaskDto

    fun toEntity(request: TaskCreateRequest, clock: Clock, entity: Task): Task
}