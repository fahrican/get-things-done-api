package com.onecosys.get_things_done.entity

import com.onecosys.get_things_done.model.Priority
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.time.LocalDateTime
import javax.persistence.*

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task")
class Task {

    @Id
    @SequenceGenerator(
        name = "task_sequence", sequenceName = "task_sequence", allocationSize = 1
    )
    @GeneratedValue(
        generator = "task_sequence", strategy = GenerationType.SEQUENCE
    )
    val id: Long = 0

    @Column(nullable = false, unique = true)
    var description: String = ""

    @Column(name = "is_reminder_set")
    var isReminderSet: Boolean = false

    @Column(name = "is_task_open")
    var isTaskOpen: Boolean = true

    @Column(name = "created_on")
    var createdOn: LocalDateTime = LocalDateTime.now()

    @Column(name = "started_on")
    var startedOn: LocalDateTime? = null

    @Column(name = "finished_on")
    var finishedOn: LocalDateTime? = null

    @Column(name = "time_interval")
    var timeInterval: String = ""

    @Column(name = "time_taken")
    var timeTaken: Int? = null

    @Enumerated(EnumType.STRING)
    var priority: Priority = Priority.LOW
}
