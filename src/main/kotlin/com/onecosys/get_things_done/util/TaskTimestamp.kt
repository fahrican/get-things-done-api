package com.onecosys.get_things_done.util

import org.springframework.stereotype.Component
import java.time.Clock

@Component
class TaskTimestamp {

    fun createClockWithZone(): Clock = Clock.systemDefaultZone()
}