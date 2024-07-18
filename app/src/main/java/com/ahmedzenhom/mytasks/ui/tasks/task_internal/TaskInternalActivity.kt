package com.ahmedzenhom.mytasks.ui.tasks.task_internal

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.activity.viewModels
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.data.model.TaskModel
import com.ahmedzenhom.mytasks.data.model.TaskStatus
import com.ahmedzenhom.mytasks.data.model.getStatusColor
import com.ahmedzenhom.mytasks.data.model.stringRes
import com.ahmedzenhom.mytasks.data.model.toFilterableTaskStatus
import com.ahmedzenhom.mytasks.databinding.ActivityTaskInternalBinding
import com.ahmedzenhom.mytasks.ui.tasks.adapter.TasksAdapter
import com.ahmedzenhom.mytasks.ui.tasks.add_edit_task.AddTaskDialog
import com.ahmedzenhom.mytasks.utils.base.BaseActivity
import com.ahmedzenhom.mytasks.utils.others.DateUtils
import com.ahmedzenhom.mytasks.utils.ui.InfoDialog
import com.ahmedzenhom.mytasks.utils.ui.select_list.SelectListSheet
import com.ahmedzenhom.mytasks.utils.ui.setIsVisible
import com.ahmedzenhom.mytasks.utils.ui.showIfNotAdded
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date


@AndroidEntryPoint
class TaskInternalActivity : BaseActivity<ActivityTaskInternalBinding, TaskInternalViewModel>() {

    companion object {
        const val TASK_ID = "TASK_ID"
        fun getIntent(context: Context, taskId: Int) =
            Intent(context, TaskInternalActivity::class.java).apply {
                putExtra(TASK_ID, taskId)
            }
    }

    override val viewModel: TaskInternalViewModel by viewModels()
    override val binding by viewBinding(ActivityTaskInternalBinding::inflate)

    private lateinit var adapter: TasksAdapter


    override fun onActivityCreated() {
        initViews()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTask()
    }

    private fun initObservers() = with(viewModel) {
        taskLiveData.observe(this@TaskInternalActivity) {
            if (it == null) {
                finish(); return@observe
            }
            bindDataToUI()
        }
    }

    private fun initViews() = with(binding) {
        registerToolBarOnBackPressed(toolbar)

        adapter = TasksAdapter { navigateToSubTaskInternal(it) }
        rvSubTasks.adapter = adapter

        clTaskStatus.setOnClickListener { showStatusSheet() }
        fabEditTask.setOnClickListener { showEditTaskDialog() }
        fabNewSubtask.setOnClickListener { showAddSubTaskDialog() }
        fabDeleteTask.setOnClickListener { showDeleteTaskSheet() }
    }

    private fun bindDataToUI() = with(viewModel.task!!) {
        with(binding) {
            tvTaskStatus.text = getString(status.stringRes())
            tvTaskTitle.text = title
            tvTaskDescription.text = description
            tvStartDate.text = DateUtils.getFormattedDate(Date(startDate))
            tvEndDate.text = DateUtils.getFormattedDate(Date(endDate))

            tvEndDate.setTextColor(
                resources.getColor(
                    if (endDate <= System.currentTimeMillis()) R.color.colorStatusRed else R.color.textColor,
                    null
                )
            )
            cvTaskStatus.setCardBackgroundColor(resources.getColor(getStatusColor(), null))

            lblSubtasks.setIsVisible(subTasks.isNotEmpty())
            adapter.submitList(subTasks)
        }
    }

    private fun showAddSubTaskDialog() {
        AddTaskDialog(
            onConfirm = { title, description, startDate, endDate ->
                viewModel.addSubTask(title, description, startDate, endDate)
            }
        ).showIfNotAdded(supportFragmentManager, AddTaskDialog.TAG)
    }

    private fun showEditTaskDialog() {
        AddTaskDialog(
            onConfirm = { title, description, startDate, endDate ->
                viewModel.updateTaskDetails(title, description, startDate, endDate)
            }, viewModel.task
        ).showIfNotAdded(supportFragmentManager, AddTaskDialog.TAG)
    }

    private fun showStatusSheet() {
        SelectListSheet(
            itemsList = TaskStatus.getFilterableTaskStatusList(this).toMutableList(),
            sheetTitle = getString(R.string.task_status),
            sheetSubTitle = getString(R.string.please_pick_status),
            selectedItem = viewModel.task?.status?.toFilterableTaskStatus(this),
        ) { status ->
            viewModel.updateTaskStatus(status?.status ?: return@SelectListSheet)
        }.showIfNotAdded(supportFragmentManager, SelectListSheet.TAG)
    }

    private fun showDeleteTaskSheet() {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = this,
            imageRes = R.drawable.warning,
            message = getDeleteTaskQuestion(),
            confirmText = getString(R.string.delete_task),
            onConfirm = { infoDialog?.dismiss();deleteTask() },
            isCancelable = true
        )
        infoDialog.showIfNotAdded(supportFragmentManager, InfoDialog.TAG)
    }

    private fun getDeleteTaskQuestion(): String {
        var question = getString(R.string.delete_task_question_p1)
        if (!viewModel.task?.subTasks.isNullOrEmpty())
            question += getString(R.string.delete_task_question_p2)
        return question
    }

    private fun deleteTask() {
        viewModel.deleteTask().observe(this) {
            showSuccessMsg(getString(R.string.task_deleted_successfully))
            finish()
        }
    }

    private fun navigateToSubTaskInternal(taskModel: TaskModel) {
        startActivity(getIntent(this, taskModel.id))
    }

}
