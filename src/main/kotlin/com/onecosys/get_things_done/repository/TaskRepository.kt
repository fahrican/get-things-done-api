package com.onecosys.get_things_done.repository

import com.onecosys.get_things_done.model.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long>