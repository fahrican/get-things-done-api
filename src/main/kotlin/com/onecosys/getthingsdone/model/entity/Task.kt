package com.onecosys.getthingsdone.model.entity

import com.onecosys.getthingsdone.model.request.MAX_DESCRIPTION_LENGTH
import com.onecosys.getthingsdone.model.request.MIN_DESCRIPTION_LENGTH
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

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