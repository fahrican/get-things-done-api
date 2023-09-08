package com.onecosys.getthingsdone

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(
    info = Info(
        title = "Task Management API v1",
        version = "1.0.0",
        description =
        "    This API exposes RESTful CRUD operations for tracking tasks. \n" +
                "    Some useful links:\n" +
                "    - [Web UI](https://justluxurylifestyle.com/app/open-tasks)\n" +
                "    - [GitHUb source code](https://github.com/fahrican/get-things-done-api)",
        license = License(name = "MIT License", url = "https://github.com/fahrican/get-things-done-api/LICENSE.txt")
    )
)
class GetThingsDoneApplication

fun main(args: Array<String>) {
    runApplication<GetThingsDoneApplication>(*args)
}
