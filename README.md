# Get Things Done Backend

[![CI](https://github.com/fahrican/get_things_done/actions/workflows/cicd.yml/badge.svg)](https://github.com/fahrican/get_things_done/actions/workflows/cicd.yml)

## This is the backend of my personal project to track all my tasks

This application is designed to expose a set of RESTful endpoints to manage tasks. It serves as the backend component of the Get Things Done application.

Before starting the project run the following command in the terminal:
```
./gradlew clean build openApiGenerate
```

Or just run:
```
./gradlew openApiGenerate
```

## Endpoints

The endpoint for all tasks can be found at: "https://task-manager.justluxurylifestyle.com/api/v1/tasks"

## Swagger UI

The Swagger UI provides a comprehensive list of the available endpoints and the request/response payloads: https://task-manager.justluxurylifestyle.com/api/swagger-ui/index.html

## Frontend

The frontend component of the application can be accessed at: https://justluxurylifestyle.com/ or https://www.justluxurylifestyle.com/
