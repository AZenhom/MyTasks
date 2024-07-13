package com.ahmedzenhom.mytasks.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ahmedzenhom.mytasks.data.model.TaskModel
import java.io.Serializable

@Entity
data class TaskEntity(
    @PrimaryKey
    val id: String,
) : Serializable

fun TaskEntity.toModel(): TaskModel = TaskModel(
    id = id,
)