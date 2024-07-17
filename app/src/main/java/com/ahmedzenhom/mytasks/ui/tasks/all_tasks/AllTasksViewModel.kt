package com.ahmedzenhom.mytasks.ui.tasks.all_tasks

import com.ahmedzenhom.mytasks.data.repository.TasksRepository
import com.ahmedzenhom.mytasks.utils.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AllTasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
) : BaseViewModel() {

}