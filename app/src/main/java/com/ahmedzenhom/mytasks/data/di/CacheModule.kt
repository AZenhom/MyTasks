package com.ahmedzenhom.mytasks.data.di

import android.content.Context
import androidx.room.Room
import com.ahmedzenhom.mytasks.data.local.datastore.AccountDataStore
import com.ahmedzenhom.mytasks.data.local.datastore.SettingsDataStore
import com.ahmedzenhom.mytasks.data.local.db.AppDatabase
import com.ahmedzenhom.mytasks.data.local.db.dao.TasksDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class
CacheModule {

    /*** DataStore */

    @Singleton
    @Provides
    fun getAccountDataStore(@ApplicationContext context: Context) = AccountDataStore(context)

    @Singleton
    @Provides
    fun getSettingsDataStore(@ApplicationContext context: Context) = SettingsDataStore(context)

    /*** Room database */

    @Singleton
    @Provides
    fun getRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "MyTasks.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun getTasksDao(database: AppDatabase): TasksDao {
        return database.tasksDao()
    }
}