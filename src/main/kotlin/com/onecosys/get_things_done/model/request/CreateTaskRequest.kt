package com.onecosys.get_things_done.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.onecosys.get_things_done.model.Priority
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank


data class CreateTaskRequest(

    @NotBlank(message = "description can't be empty")
    override val description: String,

    @JsonProperty("is_reminder_set")
    override val isReminderSet: Boolean,

    @JsonProperty("is_task_open")
    override val isTaskOpen: Boolean,

    @NotBlank(message = "created_on can't be empty")
    @JsonProperty("created_on")
    override val createdOn: LocalDateTime,

    @JsonProperty("started_on")
    override val startedOn: LocalDateTime?,

    @JsonProperty("finished_on")
    override val finishedOn: LocalDateTime?,

    @NotBlank(message = "time_interval can't be empty")
    @JsonProperty("time_interval")
    override val timeInterval: String,

    @JsonProperty("time_taken")
    override val timeTaken: Int?,

    @JsonProperty("priority")
    override val priority: Priority
) : TaskRequest
