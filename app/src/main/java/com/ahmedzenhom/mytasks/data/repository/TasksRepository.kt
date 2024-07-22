package com.ahmedzenhom.mytasks.data.repository

import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.data.local.datastore.AccountDataStore
import com.ahmedzenhom.mytasks.data.local.datastore.SettingsDataStore
import com.ahmedzenhom.mytasks.data.local.db.dao.TasksDao
import com.ahmedzenhom.mytasks.data.local.db.entities.TaskEntity
import com.ahmedzenhom.mytasks.data.local.db.entities.toModel
import com.ahmedzenhom.mytasks.data.model.TaskModel
import com.ahmedzenhom.mytasks.data.model.TaskStatus
import com.ahmedzenhom.mytasks.data.model.TaskStatus.COMPLETED
import com.ahmedzenhom.mytasks.data.model.TaskStatus.IN_PROGRESS
import com.ahmedzenhom.mytasks.data.model.TaskStatus.PENDING
import com.ahmedzenhom.mytasks.data.model.TasksSnapshotModel
import com.ahmedzenhom.mytasks.data.model.toEntity
import com.ahmedzenhom.mytasks.data.remote.firebase_rest.FirebaseRestApiService
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val tasksDao: TasksDao,
    private val settingsDataStore: SettingsDataStore? = null,
    private val accountDataStore: AccountDataStore? = null,
    private val firebaseRestApiService: FirebaseRestApiService? = null
) : BaseRepository() {

    // Syncing

    suspend fun getAllTasksAfterSync(): List<TaskModel> = execute {
        syncData()
        return@execute getAllTasks()
    }

    suspend fun getTaskByIdAfterSync(taskId: Int): TaskModel? = execute {
        syncData()
        return@execute getTaskById(taskId)
    }

    private suspend fun syncData() {
        val localTasks = getAllTasks()
        val lastLocalUpdate = settingsDataStore!!.getLastTasksLocalUpdate()
        val lastRemoteSnapshot = getLastTasksSnapshot()
        if (lastLocalUpdate > (lastRemoteSnapshot.updatedAt ?: 0)) {
            setLastTasksSnapshot(lastLocalUpdate, localTasks)
        } else if (lastLocalUpdate < (lastRemoteSnapshot.updatedAt ?: 0)) {
            tasksDao.deleteAllTask()
            tasksDao.insertTasks(
                convertTasksModelsToEntities(
                    lastRemoteSnapshot.tasks ?: emptyList()
                )
            )
        }
    }

    private suspend fun setLastLocalUpdateNow() =
        settingsDataStore?.setLastTasksLocalUpdate(System.currentTimeMillis())

    // Remote

    private suspend fun getLastTasksSnapshot(): TasksSnapshotModel {
        if (!refreshAccessTokenIfNeeded()) return TasksSnapshotModel(emptyList(), 0)
        return try {
            firebaseRestApiService!!.getLastTasksSnapshot(accountDataStore!!.getAccountModel()!!.id!!)
        } catch (e: Exception) {
            TasksSnapshotModel(emptyList(), 0)
        }
    }

    private suspend fun setLastTasksSnapshot(
        lastLocalUpdate: Long,
        localTasks: List<TaskModel>
    ): Boolean {
        if (!refreshAccessTokenIfNeeded()) return false
        return try {
            firebaseRestApiService!!.setLastTasksSnapshot(
                accountDataStore!!.getAccountModel()?.id ?: return false,
                TasksSnapshotModel(localTasks, lastLocalUpdate)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun refreshAccessTokenIfNeeded(): Boolean = execute {
        val oneHour = 60 * 60 * 1000
        if (accountDataStore!!.getAccountModel() == null) return@execute false
        if (System.currentTimeMillis() < accountDataStore.getAccessTokenLastRefresh().plus(oneHour)
        ) return@execute true
        try {
            val token =
                FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
                    ?: return@execute false
            accountDataStore.setAccessTokenLastRefresh(System.currentTimeMillis())
            accountDataStore.setAccessToken(token)
            return@execute true
        } catch (e: FirebaseNetworkException) {
            e.printStackTrace()
            return@execute false
        }
    }

    // Local

    suspend fun getAllTasks(): List<TaskModel> = execute {
        return@execute convertTasksEntitiesToModels(tasksDao.getAllTasks())
    }

    suspend fun getTaskById(id: Int): TaskModel? = execute {
        val entities = ArrayDeque<TaskEntity>()
        entities.addLast(
            tasksDao.getTaskById(id) ?: return@execute null
        )
        for (i in 0 until entities.size) {
            val subTasks = tasksDao.getSubTasksByParentId(entities[i].id)
            subTasks.forEach { entities.addLast(it) }
        }
        return@execute convertTasksEntitiesToModels(entities.toList()).firstOrNull()
    }

    suspend fun createNewTask(
        title: String,
        description: String? = null,
        startDate: Long,
        endDate: Long,
        parentTaskId: Int? = null
    ) = execute {
        var parentTaskEntity: TaskEntity? = null
        if (parentTaskId != null)
            parentTaskEntity = tasksDao.getTaskById(parentTaskId)
        val taskEntity = TaskEntity(
            parentId = parentTaskEntity?.id,
            title = title,
            description = description,
            startDate = startDate,
            endDate = endDate,
            status = if (parentTaskEntity?.status == IN_PROGRESS.value) IN_PROGRESS.value else PENDING.value,
            createdAt = System.currentTimeMillis(),
            depth = parentTaskEntity?.depth?.plus(1) ?: 0
        )
        tasksDao.insertTasks(listOf(taskEntity))
        parentTaskId?.let { adjustParentsTasksStatusAllTheWayUp(it) }
        setLastLocalUpdateNow()
    }

    suspend fun updateTaskDetails(
        taskId: Int,
        title: String,
        description: String? = null,
        startDate: Long,
        endDate: Long
    ) = execute {
        val taskEntity = tasksDao.getTaskById(taskId) ?: return@execute
        taskEntity.title = title
        taskEntity.description = description
        taskEntity.startDate = startDate
        taskEntity.endDate = endDate
        tasksDao.updateTask(taskEntity)
        setLastLocalUpdateNow()
    }

    suspend fun updateTaskStatus(taskId: Int, status: TaskStatus): Pair<Boolean, Int?> =
        execute {
            val task = tasksDao.getTaskById(taskId)
                ?: return@execute (false to R.string.something_went_wrong)
            // return if nothing to change
            if (status.value == task.status) return@execute (true to null)

            val hasChildren = tasksDao.getSubTasksByParentId(task.id).isNotEmpty()
            if (!hasChildren) {
                task.status = status.value
                tasksDao.updateTask(task)
            } else if (task.status == COMPLETED.value) {
                return@execute (false to R.string.all_subtasks_are_completed_error)
            } else { // Update status all the way down
                val tasksToUpdateDown = ArrayDeque<TaskEntity>()
                tasksToUpdateDown.addLast(task)
                while (tasksToUpdateDown.isNotEmpty()) {
                    val currentTask = tasksToUpdateDown.removeFirst()
                    val subTasks = tasksDao.getSubTasksByParentId(currentTask.id)
                    subTasks.forEach { tasksToUpdateDown.addLast(it) }
                    if (currentTask.status != COMPLETED.value) {
                        currentTask.status = status.value
                        tasksDao.updateTask(currentTask)
                    }
                }
            }
            // Update status all the way up
            task.parentId?.let { adjustParentsTasksStatusAllTheWayUp(it) }

            setLastLocalUpdateNow()
            return@execute (true to null)
        }

    suspend fun deleteTask(taskId: Int) = execute {
        val task = tasksDao.getTaskById(taskId) ?: return@execute
        val tasksToDelete = ArrayDeque<TaskEntity>()
        tasksToDelete.addLast(task)
        while (tasksToDelete.isNotEmpty()) {
            val currentTask = tasksToDelete.removeFirst()
            tasksDao.deleteTask(currentTask.id)
            val subTasks = tasksDao.getSubTasksByParentId(currentTask.id)
            subTasks.forEach { tasksToDelete.addLast(it) }
        }
        task.parentId?.let { adjustParentsTasksStatusAllTheWayUp(it) }
        setLastLocalUpdateNow()
    }

    private suspend fun adjustParentsTasksStatusAllTheWayUp(parentTaskId: Int) = execute {
        var parentTask: TaskEntity? = tasksDao.getTaskById(parentTaskId)
        while (parentTask != null) {
            val parentSubtasks = tasksDao.getSubTasksByParentId(parentTask.id)
            if (parentSubtasks.all { TaskStatus.fromInt(it.status) == COMPLETED })
                parentTask.status = COMPLETED.value
            else if (parentSubtasks.any { TaskStatus.fromInt(it.status) == IN_PROGRESS })
                parentTask.status = IN_PROGRESS.value
            else
                parentTask.status = PENDING.value
            tasksDao.updateTask(parentTask)
            parentTask = tasksDao.getTaskById(parentTask.parentId ?: break)
        }
    }

    // Utils

    private fun convertTasksModelsToEntities(list: List<TaskModel>): List<TaskEntity> {
        val entitiesList = mutableListOf<TaskEntity>()

        var currentDepthLevel = 0
        var currentLevelList = list
        while (currentLevelList.isNotEmpty()) {
            currentLevelList.forEach { model ->
                entitiesList.add(
                    model.toEntity().apply { depth = currentDepthLevel }
                )
            }
            currentDepthLevel++
            currentLevelList = currentLevelList.flatMap { it.subTasks }
        }

        return entitiesList
    }

    private fun convertTasksEntitiesToModels(sortedByDepthList: List<TaskEntity>): List<TaskModel> {
        val modelsList = mutableListOf<TaskModel>()

        val entitiesQueue = ArrayDeque<TaskEntity>()
        entitiesQueue.addAll(sortedByDepthList)

        while (entitiesQueue.isNotEmpty()) {
            val entity = entitiesQueue.removeFirst()
            var inserted = false
            for (task in modelsList) {
                if (insertSubTaskIntoTask(entity.toModel(), task)) {
                    inserted = true; break
                }
            }
            if (!inserted)
                modelsList.add(entity.toModel())
        }

        return modelsList
    }

    private fun insertSubTaskIntoTask(subTaskModel: TaskModel, task: TaskModel): Boolean {
        if (subTaskModel.parentId == task.id) {
            task.subTasks.add(subTaskModel)
            return true
        }
        task.subTasks.forEach {
            if (insertSubTaskIntoTask(subTaskModel, it)) return true
        }
        return false
    }
}