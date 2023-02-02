package com.onecosys.get_things_done

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(
    info = Info(
        title = "Task Management API",
        version = "1.0.0",
        description = "API for managing tasks"
    )
)
class GetThingsDoneApplication

fun main(args: Array<String>) {
    runApplication<GetThingsDoneApplication>(*args)
}
