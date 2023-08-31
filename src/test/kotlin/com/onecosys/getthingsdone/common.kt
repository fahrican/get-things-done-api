package com.onecosys.getthingsdone

import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

//1.5.20
fun postgres(version: String, options: JdbcDatabaseContainer<Nothing>.() -> Unit) =
    PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:$version")).apply(options)

//1.5.30 (doesn't work yet: One type argument expected for class JdbcDatabaseContainer<SELF : JdbcDatabaseContainer<SELF!>!>)
//fun postgresContainer(version: String, options: JdbcDatabaseContainer.() -> Unit) =
//    PostgreSQLContainer(DockerImageName.parse("postgres:$version")).apply(options)