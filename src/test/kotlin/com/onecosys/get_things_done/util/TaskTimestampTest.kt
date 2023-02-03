package com.onecosys.get_things_done.util

import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals

class TaskTimestampTest {

    private val zoneId = ZoneId.of("UTC")
    private val taskTimestamp = TaskTimestamp(zoneId)

    @Test
    fun `when clock with zone is created then expect specific zone id`() {
        val clock = taskTimestamp.createClockWithZone()
        assertNotNull(clock)
        assertEquals(zoneId, clock.zone)
    }

    @Test
    fun `when clock with zone is created with default value then expect system default zone id`() {
        val instant = Instant.now()
        val clock = Clock.fixed(instant, zoneId)
        val zonedDateTime = ZonedDateTime.now(clock)
        assertEquals(zoneId, zonedDateTime.zone)
        assertEquals(instant, clock.instant())
    }
}