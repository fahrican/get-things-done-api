package com.onecosys.get_things_done.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank


data class CreateTaskRequest(
        @NotBlank(message = "description can't be empty")
        val description: String,

        @JsonProperty("is_reminder_set")
        val isReminderSet: Boolean,

        @JsonProperty("is_task_open")
        val isTaskOpen: Boolean,

        @NotBlank(message = "created_on can't be empty")
        @JsonProperty("created_on")
        val createdOn: LocalDateTime,

        @JsonProperty("started_on")
        val startedOn: LocalDateTime?,

        @JsonProperty("finished_on")
        val finishedOn: LocalDateTime?,

        @NotBlank(message = "time_interval can't be empty")
        @JsonProperty("time_interval")
        val timeInterval: String,

        @JsonProperty("time_taken")
        val timeTaken: Int?
)
