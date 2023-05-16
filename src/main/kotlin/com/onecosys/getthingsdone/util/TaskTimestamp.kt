package com.onecosys.getthingsdone.util

import org.springframework.stereotype.Component
import java.time.Clock
import java.time.ZoneId

@Component
class TaskTimestamp(private val zoneId: ZoneId? = ZoneId.systemDefault()) {

    fun createClockWithZone(): Clock = Clock.system(zoneId ?: ZoneId.systemDefault())
}