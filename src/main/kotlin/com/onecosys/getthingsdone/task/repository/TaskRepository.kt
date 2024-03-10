package com.onecosys.getthingsdone.task.repository

import com.onecosys.getthingsdone.task.model.entity.Task
import com.onecosys.getthingsdone.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {

    fun findTaskByIdAndUser(id: Long, user: User): Task

    fun existsByDescription(description: String): Boolean

    fun findAllByUserAndIsTaskOpenOrderByIdAsc(user: User, isTaskOpen: Boolean): Set<Task>

    fun findAllByUserOrderByIdAsc(user: User): Set<Task>
}
