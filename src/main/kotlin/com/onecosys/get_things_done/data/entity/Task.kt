package com.onecosys.get_things_done.data.entity

import com.onecosys.get_things_done.data.model.Priority
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "task",
    uniqueConstraints = [UniqueConstraint(name = "uk_task_description", columnNames = ["description"])]
)
class Task {

    @Id
    @SequenceGenerator(
        name = "task_sequence", sequenceName = "task_sequence", allocationSize = 1
    )
    @GeneratedValue(
        generator = "task_sequence", strategy = GenerationType.SEQUENCE
    )
    val id: Long = 0

    @NotBlank
    @Column(name = "description", nullable = false, unique = true)
    var description: String = ""

    @Column(name = "is_reminder_set", nullable = false)
    var isReminderSet: Boolean = false

    @Column(name = "is_task_open", nullable = false)
    var isTaskOpen: Boolean = true

    @Column(name = "created_on", nullable = false)
    var createdOn: LocalDateTime = LocalDateTime.now()

    @Column(name = "started_on", nullable = true)
    var startedOn: LocalDateTime? = null

    @Column(name = "finished_on", nullable = true)
    var finishedOn: LocalDateTime? = null

    @Column(name = "time_interval", nullable = false)
    var timeInterval: String = ""

    @Column(name = "time_taken", nullable = true)
    var timeTaken: Int? = null

    @NotNull
    @Enumerated(EnumType.STRING)
    var priority: Priority = Priority.LOW
}
