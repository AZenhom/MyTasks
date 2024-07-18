package com.ahmedzenhom.mytasks.ui.tasks.task_internal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.data.model.TaskModel
import com.ahmedzenhom.mytasks.data.model.TaskStatus
import com.ahmedzenhom.mytasks.data.repository.TasksRepository
import com.ahmedzenhom.mytasks.utils.base.BaseViewModel
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TaskInternalViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val taskId: Int? = savedStateHandle[TaskInternalActivity.TASK_ID]
    var task: TaskModel? = null

    private val _taskLiveData: LiveEvent<TaskModel> = LiveEvent()
    val taskLiveData: LiveData<TaskModel> get() = _taskLiveData

    fun getTask() = safeLauncher {
        if (taskId == null) return@safeLauncher
        showLoading()
        task = tasksRepository.getTaskById(taskId)
        _taskLiveData.value = task
        hideLoading()
    }

    fun updateTaskDetails(
        taskTitle: String,
        taskDescription: String,
        startDate: Long,
        endDate: Long
    ) = safeLauncher {
        tasksRepository.updateTaskDetails(
            taskId!!,
            taskTitle,
            taskDescription,
            startDate,
            endDate
        )
        getTask()
        showSuccessMsg(R.string.task_updated_successfully)
    }

    fun updateTaskStatus(status: TaskStatus) = safeLauncher {
        val result = tasksRepository.updateTaskStatus(taskId!!, status)
        getTask()
        if (result.first)
            showSuccessMsg(R.string.task_updated_successfully)
        else
            showErrorMsg(result.second ?: R.string.something_went_wrong)
    }

    fun deleteTask(): LiveEvent<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()
            tasksRepository.deleteTask(taskId!!)
            liveData.value = true
            hideLoading()
        }
        return liveData
    }

    fun addSubTask(taskTitle: String, taskDescription: String, startDate: Long, endDate: Long) =
        safeLauncher {
            tasksRepository.createNewTask(
                taskTitle,
                taskDescription,
                startDate,
                endDate,
                taskId
            )
            showSuccessMsg(R.string.task_created_successfully)
            getTask()
        }
}