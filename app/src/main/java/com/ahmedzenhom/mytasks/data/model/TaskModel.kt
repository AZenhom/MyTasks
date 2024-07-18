package com.ahmedzenhom.mytasks.data.model

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.data.local.db.entities.TaskEntity
import com.ahmedzenhom.mytasks.utils.others.Filterable
import java.io.Serializable

data class TaskModel(
    val id: Int,
    val parentId: Int? = null,
    var title: String,
    var description: String? = null,
    var startDate: Long,
    var endDate: Long,
    var status: TaskStatus,
    val createdAt: Long,
    private var _subTasks: MutableList<TaskModel>? = mutableListOf(),
) : Serializable {
    val subTasks: MutableList<TaskModel>
        get() {
            if (_subTasks == null) {
                _subTasks = mutableListOf()
            }
            return _subTasks!!
        }
}

data class TasksSnapshotModel(
    val tasks: List<TaskModel>? = null,
    val updatedAt: Long? = null,
) : Serializable

fun TaskModel.toEntity() = TaskEntity(
    id = id,
    parentId = parentId,
    title = title,
    description = description,
    startDate = startDate,
    endDate = endDate,
    status = status.value,
    createdAt = createdAt,
)

enum class TaskStatus(val value: Int) {
    PENDING(0),
    IN_PROGRESS(1),
    COMPLETED(2);

    companion object {
        fun fromInt(value: Int?) = entries.firstOrNull { it.value == value } ?: PENDING

        fun getFilterableTaskStatusList(context: Context) =
            TaskStatus.entries.map { it.toFilterableTaskStatus(context) }
    }


}

fun TaskStatus.toFilterableTaskStatus(context: Context) = FilterableTaskStatus(context, this)

data class FilterableTaskStatus(
    @Transient
    var context: Context,
    val status: TaskStatus,
) : Serializable, Filterable {
    override fun getFilterableId(): String {
        return context.getString(status.stringRes())
    }

    override fun getFilterableName(): String? {
        return context.getString(status.stringRes())
    }
}

@StringRes
fun TaskStatus.stringRes(): Int = when (this) {
    TaskStatus.PENDING -> R.string.pending
    TaskStatus.IN_PROGRESS -> R.string.in_progress
    TaskStatus.COMPLETED -> R.string.completed
}

@ColorRes
fun TaskModel.getStatusColor(): Int {
    return status.colorRes(endDate < System.currentTimeMillis())
}

@ColorRes
private fun TaskStatus.colorRes(isExpired: Boolean = false) =
    if (isExpired) R.color.colorStatusRed else when (this) {
        TaskStatus.PENDING -> R.color.colorStatusGray
        TaskStatus.IN_PROGRESS -> R.color.colorStatusOrange
        TaskStatus.COMPLETED -> R.color.colorStatusGreen
    }
