package com.ahmedzenhom.mytasks.ui.tasks.add_edit_task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.data.model.TaskModel
import com.ahmedzenhom.mytasks.databinding.DialogAddTaskLayoutBinding
import com.ahmedzenhom.mytasks.utils.others.DateUtils
import es.dmoral.toasty.Toasty
import java.util.Date

class AddTaskDialog(
    private val onConfirm: ((title: String, description: String, startDate: Long, endDate: Long) -> Unit)? = null,
    private val taskToEdit: TaskModel? = null,
) : DialogFragment() {

    companion object {
        const val TAG = "AddTaskDialog"
    }

    private var _binding: DialogAddTaskLayoutBinding? = null
    private val binding get() = _binding!!

    private var startDate: Long = 0
    private var endDate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppDialogStyle)
        super.setCancelable(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddTaskLayoutBinding.inflate(inflater)
        initViews()
        return binding.root
    }

    private fun initViews() = with(binding) {
        startDate = System.currentTimeMillis()
        endDate = System.currentTimeMillis() + 86400000 // (24 * 60 * 60 * 1000)
        if (taskToEdit != null) {
            lblMessage.text = getString(R.string.edit_task)
            etTaskTitle.setText(taskToEdit.title)
            etTaskDescription.setText(taskToEdit.description)
            startDate = taskToEdit.startDate
            endDate = taskToEdit.endDate
        }
        refreshStartAndEndDateText()
        // Click Listeners
        clStartDate.setOnClickListener {
            DateUtils.pickDateAndTime(requireContext(), Date(startDate))
                .observe(viewLifecycleOwner) {
                    startDate = it.time
                    refreshStartAndEndDateText()
                }
        }
        clEndDate.setOnClickListener {
            DateUtils.pickDateAndTime(requireContext(), Date(endDate)).observe(viewLifecycleOwner) {
                endDate = it.time
                refreshStartAndEndDateText()
            }
        }
        btnConfirm.setOnClickListener {
            val taskTitle = etTaskTitle.text.toString().trim()
            val taskDescription = etTaskDescription.text.toString().trim()
            if (taskTitle.isEmpty()) {
                Toasty.error(
                    requireContext(),
                    getString(R.string.invalid_task_title), Toasty.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            if (startDate >= endDate) {
                Toasty.error(
                    requireContext(),
                    getString(R.string.invalid_date_range), Toasty.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            dismiss()
            onConfirm?.invoke(taskTitle, taskDescription, startDate, endDate)
        }
        btnCancel.setOnClickListener { dismiss() }
    }

    private fun refreshStartAndEndDateText() {
        val startDate = DateUtils.getFormattedDate(Date(this.startDate))
        val endDate = DateUtils.getFormattedDate(Date(this.endDate))
        binding.tvStartDate.text = startDate
        binding.tvEndDate.text = endDate
    }
}