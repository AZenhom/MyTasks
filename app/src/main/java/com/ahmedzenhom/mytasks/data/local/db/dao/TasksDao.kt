package com.ahmedzenhom.mytasks.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ahmedzenhom.mytasks.data.local.db.entities.TaskEntity

@Dao
interface TasksDao {
    @Query("select * FROM TaskEntity ORDER BY depth ASC, createdAt DESC")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("select * FROM TaskEntity where id = :id limit 1")
    suspend fun getTaskById(id: Int): TaskEntity?

    @Query("select * FROM TaskEntity where parentId = :id")
    suspend fun getSubTasksByParentId(id: Int): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(list: List<TaskEntity>)

    @Update
    suspend fun updateTask(user: TaskEntity)

    @Query("delete from TaskEntity where id = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query("delete from TaskEntity")
    suspend fun deleteAllTask()
}