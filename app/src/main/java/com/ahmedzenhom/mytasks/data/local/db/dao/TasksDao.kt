package com.ahmedzenhom.mytasks.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ahmedzenhom.mytasks.data.local.db.entities.TaskEntity

@Dao
interface TasksDao {
    @Query("select * FROM TaskEntity")
    suspend fun getAllTasks(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(list: List<TaskEntity>)

    @Query("delete from TaskEntity where id = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("delete from TaskEntity")
    suspend fun deleteAllTask()
}