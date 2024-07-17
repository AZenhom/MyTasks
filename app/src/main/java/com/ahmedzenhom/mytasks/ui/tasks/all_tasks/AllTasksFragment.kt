package com.ahmedzenhom.mytasks.ui.tasks.all_tasks

import android.util.Log
import androidx.fragment.app.viewModels
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.databinding.FragmentAllTasksBinding
import com.ahmedzenhom.mytasks.ui.tasks.add_edit_task.AddTaskDialog
import com.ahmedzenhom.mytasks.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllTasksFragment :
    BaseFragment<FragmentAllTasksBinding, AllTasksViewModel>(R.layout.fragment_all_tasks) {

    override val viewModel: AllTasksViewModel by viewModels()
    override val binding by viewBinding(FragmentAllTasksBinding::bind)

    override fun onViewCreated() {
        initViews()
    }

    private fun initViews() = with(binding) {
        fab.setOnClickListener { showAddTaskDialog() }
    }

    private fun showAddTaskDialog() {
        AddTaskDialog(
            onConfirm = { title, description, startDate, endDate ->
                Log.d("AddTaskDialog", "onConfirm: $title, $description, $startDate, $endDate")
            }
        ).show(childFragmentManager, AddTaskDialog.TAG)
    }
}