package com.onecosys.get_things_done.util

import com.onecosys.get_things_done.model.entity.Task
import com.onecosys.get_things_done.model.dto.TaskDto
import com.onecosys.get_things_done.model.request.TaskCreateRequest
import java.time.Clock

interface TaskMapper {

    fun toDto(entity: Task): TaskDto

    fun toEntity(request: TaskCreateRequest, clock: Clock, entity: Task): Task
}