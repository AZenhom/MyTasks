package com.ahmedzenhom.mytasks.ui.tasks.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmedzenhom.mytasks.data.model.TaskModel
import com.ahmedzenhom.mytasks.data.model.getStatusColor
import com.ahmedzenhom.mytasks.databinding.ItemTaskBinding
import com.ahmedzenhom.mytasks.utils.others.DateUtils
import java.util.Date


class TasksAdapter(
    private val onItemClick: ((model: TaskModel) -> Unit)? = null,
) : ListAdapter<TaskModel, TasksAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: TaskModel) {
            with(binding) {
                // Data
                tvTaskTitle.text = item.title
                tvStartDate.text = DateUtils.getFormattedDate(Date(item.startDate))
                tvEndDate.text = DateUtils.getFormattedDate(Date(item.endDate))
                vTaskStatus.setBackgroundColor(vTaskStatus.context.getColor(item.getStatusColor()))
                // Click Listeners
                clRootView.setOnClickListener { onItemClick?.invoke(item) }

            }
        }
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TaskModel>() {
            override fun areItemsTheSame(
                oldItem: TaskModel,
                newItem: TaskModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: TaskModel,
                newItem: TaskModel
            ): Boolean =
                oldItem.id == newItem.id &&
                        oldItem.title == newItem.title &&
                        oldItem.startDate == newItem.startDate &&
                        oldItem.endDate == newItem.endDate &&
                        oldItem.getStatusColor() == newItem.getStatusColor()
        }
    }

}