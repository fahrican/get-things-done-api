package com.onecosys.getthingsdone.model.entity

import com.onecosys.getthingsdone.model.Priority
import com.onecosys.getthingsdone.model.dto.MAX_DESCRIPTION_LENGTH
import com.onecosys.getthingsdone.model.dto.MIN_DESCRIPTION_LENGTH
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(
        name = "task",
        uniqueConstraints = [UniqueConstraint(name = "uk_task_description", columnNames = ["description"])]
)
class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_sequence")
    @SequenceGenerator(name = "task_sequence", sequenceName = "task_sequence", allocationSize = 1)
    val id: Long = 0

    @Size(min = MIN_DESCRIPTION_LENGTH, max = MAX_DESCRIPTION_LENGTH)
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
