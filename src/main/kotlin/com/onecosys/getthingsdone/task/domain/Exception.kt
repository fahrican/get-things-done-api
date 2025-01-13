package com.onecosys.getthingsdone.task.domain

import org.springframework.modulith.ApplicationModule

@ApplicationModule(type = ApplicationModule.Type.OPEN)
class TaskNotFoundException(message: String) : RuntimeException(message)

class BadTaskRequestException(message: String) : RuntimeException(message)
