package com.onecosys.get_things_done.model

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.time.LocalDateTime
import javax.persistence.*

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task")
data class Task(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private var id: Long,

        val description: String,

        @Column(name = "is_reminder_set")
        var isReminderSet: Boolean = false,

        @Column(name = "is_task_open")
        var isTaskOpen: Boolean = true,

        @Column(name = "created_on")
        val createdOn: LocalDateTime,

        @Column(name = "started_on")
        var startedOn: LocalDateTime?,

        @Column(name = "finished_on")
        var finishedOn: LocalDateTime?,

        @Column(name = "time_interval")
        val timeInterval: String,

        @Column(name = "time_taken")
        var timeTaken: Int?
) {
    val taskId: Long get() = id
}
