package com.onecosys.get_things_done.repository

import com.onecosys.get_things_done.model.entity.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {

    fun findTaskById(id: Long): Task

    fun findAllByIsTaskOpenOrderByIdAsc(isTaskOpen: Boolean): List<Task>

    fun findAllByOrderByIdAsc(): List<Task>

    fun existsByDescription(description: String): Boolean
}