package com.onecosys.getthingsdone.repository

import com.onecosys.getthingsdone.model.entity.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {

    fun findTaskById(id: Long): Task

    fun findAllByIsTaskOpenOrderByIdAsc(isTaskOpen: Boolean): Set<Task>

    fun findAllByOrderByIdAsc(): Set<Task>

    fun existsByDescription(description: String): Boolean
}