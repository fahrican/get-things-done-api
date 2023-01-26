package com.onecosys.get_things_done.repository

import com.onecosys.get_things_done.model.entity.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {

    fun findTaskById(id: Long): Task

    @Query(value = "SELECT * FROM task WHERE is_task_open = TRUE ORDER BY id ASC", nativeQuery = true)
    fun queryAllOpenTasks(): List<Task>

    @Query(value = "SELECT * FROM task WHERE is_task_open = FALSE ORDER BY id ASC", nativeQuery = true)
    fun queryAllClosedTasks(): List<Task>

    @Query(value = "SELECT t FROM Task t ORDER BY t.id ASC")
    fun queryAllTasks(): List<Task>

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END FROM Task t WHERE t.description = ?1")
    fun doesDescriptionExist(description: String): Boolean
}