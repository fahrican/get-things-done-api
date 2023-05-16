package com.onecosys.getthingsdone.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class TaskTimestampTest {

    private val zoneId = ZoneId.of("UTC")
    private val taskTimestamp = TaskTimestamp(zoneId)

    @Test
    fun `when clock with zone is created then expect specific zone id`() {
        val clock = taskTimestamp.createClockWithZone()

        assertThat(clock).isNotNull
        assertThat(clock.zone).isEqualTo(zoneId)
    }

    @Test
    fun `when clock with zone is created with default value then expect system default zone id`() {
        val instant = Instant.now()
        val clock = Clock.fixed(instant, zoneId)
        val zonedDateTime = ZonedDateTime.now(clock)

        assertThat(zonedDateTime.zone).isEqualTo(zoneId)
        assertThat(clock.instant()).isEqualTo(instant)
    }

    @Test
    fun `when clock with different zone is created then expect specific zone id`() {
        val differentZoneId = ZoneId.of("Europe/Paris")
        val taskTimestamp = TaskTimestamp(differentZoneId)
        val clock = taskTimestamp.createClockWithZone()

        assertThat(clock).isNotNull
        assertThat(clock.zone).isEqualTo(differentZoneId)
    }

    @Test
    fun `when clock with non-default zone is created then expect non-default zone id`() {
        val nonDefaultZoneId = ZoneId.of("Asia/Kolkata")
        val taskTimestamp = TaskTimestamp(nonDefaultZoneId)
        val clock = taskTimestamp.createClockWithZone()

        assertThat(clock).isNotNull
        assertThat(clock.zone).isNotEqualTo(ZoneId.systemDefault())
        assertThat(clock.zone).isEqualTo(nonDefaultZoneId)
    }

    @Test
    fun `when clock with null zone is created then expect default zone id`() {
        val taskTimestamp = TaskTimestamp(null)
        val clock = taskTimestamp.createClockWithZone()

        assertThat(clock).isNotNull
        assertThat(clock.zone).isEqualTo(ZoneId.systemDefault())
    }
}
