package com.ahmedzenhom.mytasks.data.model

import androidx.annotation.ColorRes
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.data.local.db.entities.TaskEntity
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
    val subTasks: MutableList<TaskModel> = mutableListOf(),
) : Serializable

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
    }
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
        TaskStatus.COMPLETED -> R.color.colorStatusRed
    }
