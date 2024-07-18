package com.ahmedzenhom.mytasks.ui.tasks.all_tasks

import androidx.fragment.app.viewModels
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.data.model.TaskModel
import com.ahmedzenhom.mytasks.databinding.FragmentAllTasksBinding
import com.ahmedzenhom.mytasks.ui.tasks.adapter.TasksAdapter
import com.ahmedzenhom.mytasks.ui.tasks.add_edit_task.AddTaskDialog
import com.ahmedzenhom.mytasks.ui.tasks.task_internal.TaskInternalActivity
import com.ahmedzenhom.mytasks.utils.base.BaseFragment
import com.ahmedzenhom.mytasks.utils.ui.showIfNotAdded
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllTasksFragment :
    BaseFragment<FragmentAllTasksBinding, AllTasksViewModel>(R.layout.fragment_all_tasks) {

    override val viewModel: AllTasksViewModel by viewModels()
    override val binding by viewBinding(FragmentAllTasksBinding::bind)

    private lateinit var tasksAdapter: TasksAdapter

    override fun onViewCreated() {
        initViews()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        getAllTasks()
    }

    private fun getAllTasks() = viewModel.getTasks()

    private fun initViews() = with(binding) {
        toolbar.tvTitle.text = getString(R.string.all_tasks)

        tasksAdapter = TasksAdapter { navigateToTaskInternal(it) }
        rvTasks.adapter = tasksAdapter

        fab.setOnClickListener { showAddTaskDialog() }
        refreshLayout.setOnRefreshListener { getAllTasks() }
    }

    private fun initObservers() = with(viewModel) {
        tasksLiveData.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun showAddTaskDialog() {
        AddTaskDialog(
            onConfirm = { title, description, startDate, endDate ->
                viewModel.createNewTask(title, description, startDate, endDate)
                    .observe(viewLifecycleOwner) { showSuccessMsg(getString(R.string.task_created_successfully)) }
            }
        ).showIfNotAdded(childFragmentManager, AddTaskDialog.TAG)
    }

    private fun navigateToTaskInternal(taskModel: TaskModel) {
        startActivity(TaskInternalActivity.getIntent(requireContext(), taskModel.id))
    }
}