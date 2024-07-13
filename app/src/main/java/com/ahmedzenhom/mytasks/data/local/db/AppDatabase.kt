package com.ahmedzenhom.mytasks.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ahmedzenhom.mytasks.data.local.db.dao.TasksDao
import com.ahmedzenhom.mytasks.data.local.db.entities.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tasksDao(): TasksDao
}