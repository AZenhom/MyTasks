package com.ahmedzenhom.mytasks.ui.tasks.all_tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ahmedzenhom.mytasks.data.model.TaskModel
import com.ahmedzenhom.mytasks.data.repository.TasksRepository
import com.ahmedzenhom.mytasks.utils.base.BaseViewModel
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AllTasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
) : BaseViewModel() {

    private val _tasksLiveData: MutableLiveData<List<TaskModel>> = MutableLiveData()
    val tasksLiveData: LiveData<List<TaskModel>> get() = _tasksLiveData

    fun getTasks() = safeLauncher {
        showLoading()
        _tasksLiveData.value = tasksRepository.getAllTasksAfterSync()
        hideLoading()
    }

    fun createNewTask(
        taskTitle: String,
        taskDescription: String,
        startDate: Long,
        endDate: Long
    ): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            tasksRepository.createNewTask(taskTitle, taskDescription, startDate, endDate)
            getTasks()
            liveData.value = true
        }
        return liveData
    }
}