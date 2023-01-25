package com.onecosys.get_things_done.util

import com.onecosys.get_things_done.data.entity.Task
import com.onecosys.get_things_done.data.dto.TaskDto
import com.onecosys.get_things_done.data.request.TaskCreateRequest
import java.time.Clock

interface TaskMapper {

    fun toDto(entity: Task): TaskDto

    fun toEntity(request: TaskCreateRequest, clock: Clock, entity: Task): Task
}