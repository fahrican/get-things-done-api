package com.onecosys.getthingsdone

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(
    info = Info(
        title = "Task Management API v1",
        version = "1.0.0",
        description =
        "This API exposes RESTful CRUD operations for tracking tasks.\n" +
                "\n" +
                "Some useful links:\n" +
                "- [Web UI](https://justluxurylifestyle.com/app/open-tasks)\n" +
                "- [GitHUb source code](https://github.com/fahrican/get-things-done-api)",
        license = License(
            name = "MIT License",
            url = "https://github.com/fahrican/get-things-done-api/blob/master/LICENSE.txt"
        )
    ),
    servers = [
        Server(
            description = "DEV ENV",
            url = "http://localhost:9091"
        ),
        Server(
            description = "PROD ENV",
            url = "https://task-manager.justluxurylifestyle.com/"
        ),
    ],
    security = [
        SecurityRequirement(
            name = "bearerAuth"
        )
    ]
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT authentication description",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    `in` = SecuritySchemeIn.HEADER
)
class GetThingsDoneApplication

fun main(args: Array<String>) {
    runApplication<GetThingsDoneApplication>(*args)
}
