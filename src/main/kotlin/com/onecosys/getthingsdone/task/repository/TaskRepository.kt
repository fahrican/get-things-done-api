package com.onecosys.getthingsdone.task.repository

import com.onecosys.getthingsdone.task.entity.Task
import com.onecosys.getthingsdone.user.entity.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {

    fun findTaskByIdAndAppUser(id: Long, appUser: AppUser): Task

    fun existsByDescription(description: String): Boolean

    fun findAllByAppUserAndIsTaskOpenOrderByIdAsc(appUser: AppUser, isTaskOpen: Boolean): Set<Task>

    fun findAllByAppUserOrderByIdAsc(appUser: AppUser): Set<Task>
}
