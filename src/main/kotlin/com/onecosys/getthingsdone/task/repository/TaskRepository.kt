package com.onecosys.getthingsdone.task.repository

import com.onecosys.getthingsdone.task.entity.Task
import com.onecosys.getthingsdone.user.entity.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.appUser = ?1 ORDER BY t.id ASC")
    fun findTaskByIdAndUser(id: Long, appUser: AppUser): Task?

    @Query("SELECT t FROM Task t WHERE t.description = ?1")
    fun existsByDescription(description: String): Boolean

    @Query("SELECT t FROM Task t WHERE t.appUser = ?1 AND t.isTaskOpen = ?2 ORDER BY t.id ASC")
    fun findAllByUserAndIsTaskOpenOrderByIdAsc(appUser: AppUser, isTaskOpen: Boolean): Set<Task>

    @Query("SELECT t FROM Task t WHERE t.appUser = ?1 ORDER BY t.id ASC")
    fun findAllByUserOrderByIdAsc(appUser: AppUser): Set<Task>
}
