package com.onecosys.getthingsdone

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

class ModularityTest {

    companion object {
        val modules = ApplicationModules.of(GetThingsDoneApplication::class.java)
    }

    @Test
    fun `print modules`() {
        ApplicationModules.of(GetThingsDoneApplication::class.java).forEach { println(it) }
    }

    @Test
    fun `verifies modular structure`() {
        modules.verify()
    }
}