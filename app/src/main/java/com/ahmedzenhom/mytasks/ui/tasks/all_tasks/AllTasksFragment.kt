package com.ahmedzenhom.mytasks.ui.tasks.all_tasks

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.databinding.FragmentAllTasksBinding
import com.ahmedzenhom.mytasks.ui.host.HostViewModel
import com.ahmedzenhom.mytasks.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
    class AllTasksFragment :
        BaseFragment<FragmentAllTasksBinding, AllTasksViewModel>(R.layout.fragment_all_tasks) {

        override val viewModel: AllTasksViewModel by viewModels()
        override val binding by viewBinding(FragmentAllTasksBinding::bind)
        private val hostViewModel: HostViewModel by activityViewModels()

        override fun onViewCreated() {
            showSuccessMsg("All Tasks")
        }
}