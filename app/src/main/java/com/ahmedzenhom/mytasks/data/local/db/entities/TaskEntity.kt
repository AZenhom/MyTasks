package com.ahmedzenhom.mytasks.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ahmedzenhom.mytasks.data.model.TaskModel
import com.ahmedzenhom.mytasks.data.model.TaskStatus
import java.io.Serializable

@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val parentId: Int? = null,
    var title: String,
    var description: String? = null,
    var startDate: Long,
    var endDate: Long,
    var status: Int,
    val createdAt: Long,
    var depth: Int = 0
) : Serializable

fun TaskEntity.toModel(): TaskModel = TaskModel(
    id = id,
    parentId = parentId,
    title = title,
    description = description,
    startDate = startDate,
    endDate = endDate,
    status = TaskStatus.fromInt(status),
    createdAt = createdAt,
)