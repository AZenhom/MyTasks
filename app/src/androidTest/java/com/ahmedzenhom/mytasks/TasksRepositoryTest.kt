package com.ahmedzenhom.mytasks

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ahmedzenhom.mytasks.data.local.db.AppDatabase
import com.ahmedzenhom.mytasks.data.model.TaskStatus
import com.ahmedzenhom.mytasks.data.repository.TasksRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var tasksRepository: TasksRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        val tasksDao = db.tasksDao()
        tasksRepository = TasksRepository(tasksDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTask() = runBlocking {
        tasksRepository.createNewTask(title = "Task 1", startDate = 1, endDate = 2)
        val tasks = tasksRepository.getAllTasks()
        assertEquals(tasks.size, 1)
        assertNotNull(tasksRepository.getTaskById(tasks.first().id))
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetMultilevelTasks() = runBlocking {
        tasksRepository.createNewTask(title = "Task 1", startDate = 1, endDate = 2)
        tasksRepository.createNewTask(title = "Task 2", startDate = 1, endDate = 2)
        tasksRepository.createNewTask(title = "Task 3", startDate = 1, endDate = 2)
        var tasks = tasksRepository.getAllTasks()
        assertEquals(tasks.size, 3)

        tasksRepository.createNewTask(
            title = "Task 1 - 1",
            startDate = 1,
            endDate = 2,
            parentTaskId = tasks.last().id
        )
        tasksRepository.createNewTask(
            title = "Task 1 - 2",
            startDate = 1,
            endDate = 2,
            parentTaskId = tasks.last().id
        )
        tasks = tasksRepository.getAllTasks()
        assertEquals(tasks.size, 3)
        assertEquals(tasks.last().subTasks.size, 2)

        val task = tasksRepository.getTaskById(tasks.last().id)
        assertNotNull(task)
        assertEquals(task!!.subTasks.size, 2)
    }

    @Test
    @Throws(Exception::class)
    fun updateTaskDetails() = runBlocking {
        tasksRepository.createNewTask(title = "Task 1", startDate = 1, endDate = 2)
        tasksRepository.createNewTask(title = "Task 2", startDate = 3, endDate = 4)
        tasksRepository.createNewTask(title = "Task 3", startDate = 5, endDate = 6)

        var task = tasksRepository.getAllTasks().first()
        assertEquals(task.title, "Task 3")
        assertEquals(task.startDate, 5)
        assertEquals(task.endDate, 6)

        tasksRepository.updateTaskDetails(
            task.id,
            "Task 3 Edited",
            task.description,
            10,
            11
        )

        task = tasksRepository.getAllTasks().first()
        assertEquals(task.title, "Task 3 Edited")
        assertEquals(task.startDate, 10)
        assertEquals(task.endDate, 11)
    }

    @Test
    @Throws(Exception::class)
    fun deleteTask() = runBlocking {
        tasksRepository.createNewTask(title = "Task 1", startDate = 1, endDate = 2)
        tasksRepository.createNewTask(title = "Task 2", startDate = 1, endDate = 2)
        tasksRepository.createNewTask(title = "Task 3", startDate = 1, endDate = 2)

        var tasks = tasksRepository.getAllTasks()
        tasksRepository.deleteTask(tasks.first().id)
        tasks = tasksRepository.getAllTasks()
        assertEquals(tasks.size, 2)

        tasksRepository.createNewTask(
            title = "Task 1 - 1",
            startDate = 1,
            endDate = 2,
            parentTaskId = tasks.last().id
        )
        tasksRepository.createNewTask(
            title = "Task 1 - 2",
            startDate = 1,
            endDate = 2,
            parentTaskId = tasks.last().id
        )
        val task = tasksRepository.getTaskById(tasks.last().id)!!
        assertNotNull(tasksRepository.getTaskById(task.subTasks.first().id))
        tasksRepository.deleteTask(task.id)
        assertNull(tasksRepository.getTaskById(task.subTasks.first().id))
    }


    @Test
    @Throws(Exception::class)
    fun updateTaskStatus() = runBlocking {
        tasksRepository.createNewTask(title = "Task 1", startDate = 1, endDate = 2)
        tasksRepository.createNewTask(title = "Task 2", startDate = 3, endDate = 4)
        tasksRepository.createNewTask(title = "Task 3", startDate = 5, endDate = 6)

        var tasks = tasksRepository.getAllTasks()
        assertEquals(tasks.last().status, TaskStatus.PENDING)

        tasksRepository.createNewTask(
            title = "Task 1 - 1",
            startDate = 1,
            endDate = 2,
            parentTaskId = tasks.last().id
        )
        tasks = tasksRepository.getAllTasks()
        assertEquals(tasks.last().subTasks.first().status, TaskStatus.PENDING)

        tasksRepository.updateTaskStatus(tasks.first().id, TaskStatus.IN_PROGRESS)
        tasksRepository.createNewTask(
            title = "Task 3 - 1",
            startDate = 1,
            endDate = 2,
            parentTaskId = tasks.first().id
        )
        tasks = tasksRepository.getAllTasks()
        assertEquals(tasks.first().subTasks.first().status, TaskStatus.IN_PROGRESS)
    }


    @Test
    @Throws(Exception::class)
    fun updateTaskStatusDeepLevels() = runBlocking {
        tasksRepository.createNewTask(title = "Task 1", startDate = 1, endDate = 2) // ID 1
        tasksRepository.createNewTask(title = "Task 2", startDate = 1, endDate = 2) // ID 2

        tasksRepository.createNewTask(title = "Task 1 - 1", startDate = 1, endDate = 2, parentTaskId = 1) // ID 3
        tasksRepository.createNewTask(title = "Task 1 - 2", startDate = 1, endDate = 2, parentTaskId = 1) // ID 4
        tasksRepository.createNewTask(title = "Task 1 - 3", startDate = 1, endDate = 2, parentTaskId = 1) // ID 5

        tasksRepository.createNewTask(title = "Task 1 - 1 - 1", startDate = 1, endDate = 2, parentTaskId = 3) // ID 6
        tasksRepository.createNewTask(title = "Task 1 - 1 - 2", startDate = 1, endDate = 2, parentTaskId = 3) // ID 7
        tasksRepository.createNewTask(title = "Task 1 - 1 - 3", startDate = 1, endDate = 2, parentTaskId = 3) // ID 8


        assertTrue(tasksRepository.updateTaskStatus(3, TaskStatus.IN_PROGRESS).first)
        assertEquals(tasksRepository.getTaskById(1)!!.status, TaskStatus.IN_PROGRESS)
        assertEquals(tasksRepository.getTaskById(2)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(3)!!.status, TaskStatus.IN_PROGRESS)
        assertEquals(tasksRepository.getTaskById(4)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(5)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(6)!!.status, TaskStatus.IN_PROGRESS)
        assertEquals(tasksRepository.getTaskById(7)!!.status, TaskStatus.IN_PROGRESS)
        assertEquals(tasksRepository.getTaskById(8)!!.status, TaskStatus.IN_PROGRESS)


        assertTrue(tasksRepository.updateTaskStatus(6, TaskStatus.COMPLETED).first)
        assertEquals(tasksRepository.getTaskById(1)!!.status, TaskStatus.IN_PROGRESS)
        assertEquals(tasksRepository.getTaskById(2)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(3)!!.status, TaskStatus.IN_PROGRESS)
        assertEquals(tasksRepository.getTaskById(4)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(5)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(6)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(7)!!.status, TaskStatus.IN_PROGRESS)
        assertEquals(tasksRepository.getTaskById(8)!!.status, TaskStatus.IN_PROGRESS)

        assertTrue(tasksRepository.updateTaskStatus(7, TaskStatus.COMPLETED).first)
        assertEquals(tasksRepository.getTaskById(1)!!.status, TaskStatus.IN_PROGRESS)
        assertEquals(tasksRepository.getTaskById(2)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(3)!!.status, TaskStatus.IN_PROGRESS)
        assertEquals(tasksRepository.getTaskById(4)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(5)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(6)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(7)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(8)!!.status, TaskStatus.IN_PROGRESS)

        assertTrue(tasksRepository.updateTaskStatus(8, TaskStatus.COMPLETED).first)
        assertEquals(tasksRepository.getTaskById(1)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(2)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(3)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(4)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(5)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(6)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(7)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(8)!!.status, TaskStatus.COMPLETED)

        assertTrue(tasksRepository.updateTaskStatus(1, TaskStatus.IN_PROGRESS).first)
        assertEquals(tasksRepository.getTaskById(1)!!.status, TaskStatus.IN_PROGRESS)
        assertEquals(tasksRepository.getTaskById(2)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(3)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(4)!!.status, TaskStatus.IN_PROGRESS)
        assertEquals(tasksRepository.getTaskById(5)!!.status, TaskStatus.IN_PROGRESS)
        assertEquals(tasksRepository.getTaskById(6)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(7)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(8)!!.status, TaskStatus.COMPLETED)

        assertTrue(tasksRepository.updateTaskStatus(1, TaskStatus.PENDING).first)
        assertEquals(tasksRepository.getTaskById(1)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(2)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(3)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(4)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(5)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(6)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(7)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(8)!!.status, TaskStatus.COMPLETED)

        assertTrue(tasksRepository.updateTaskStatus(1, TaskStatus.COMPLETED).first)
        assertEquals(tasksRepository.getTaskById(1)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(2)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(3)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(4)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(5)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(6)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(7)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(8)!!.status, TaskStatus.COMPLETED)

        assertTrue(tasksRepository.updateTaskStatus(2, TaskStatus.COMPLETED).first)
        assertEquals(tasksRepository.getTaskById(1)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(2)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(3)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(4)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(5)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(6)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(7)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(8)!!.status, TaskStatus.COMPLETED)

        assertTrue(tasksRepository.updateTaskStatus(2, TaskStatus.PENDING).first)
        assertEquals(tasksRepository.getTaskById(1)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(2)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(3)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(4)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(5)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(6)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(7)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(8)!!.status, TaskStatus.COMPLETED)

        assertFalse(tasksRepository.updateTaskStatus(3, TaskStatus.IN_PROGRESS).first)
        assertEquals(tasksRepository.getTaskById(1)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(2)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(3)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(4)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(5)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(6)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(7)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(8)!!.status, TaskStatus.COMPLETED)

        assertTrue(tasksRepository.updateTaskStatus(4, TaskStatus.PENDING).first)
        assertEquals(tasksRepository.getTaskById(1)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(2)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(3)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(4)!!.status, TaskStatus.PENDING)
        assertEquals(tasksRepository.getTaskById(5)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(6)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(7)!!.status, TaskStatus.COMPLETED)
        assertEquals(tasksRepository.getTaskById(8)!!.status, TaskStatus.COMPLETED)

    }


}