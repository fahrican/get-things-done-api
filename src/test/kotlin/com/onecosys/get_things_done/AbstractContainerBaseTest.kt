package com.onecosys.get_things_done

import org.testcontainers.containers.MySQLContainer

abstract class AbstractContainerBaseTest {

    companion object {
        val mySQLContainer = MySQLContainer("mysql:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true)
    }

    init {
        mySQLContainer.start()
    }
}