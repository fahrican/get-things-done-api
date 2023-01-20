package com.onecosys.get_things_done.repository

import com.onecosys.get_things_done.data.entity.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {

    //TODO: Add query for all tasks and use for all ASC for ID
    fun findTaskById(id: Long): Task

    @Query(value = "SELECT * FROM task WHERE is_task_open = TRUE", nativeQuery = true)
    fun queryAllOpenTasks(): List<Task>

    @Query(value = "SELECT * FROM task WHERE is_task_open = FALSE", nativeQuery = true)
    fun queryAllClosedTasks(): List<Task>

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END FROM Task t WHERE t.description = ?1")
    fun doesDescriptionExist(description: String): Boolean
}