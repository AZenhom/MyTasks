package com.ahmedzenhom.mytasks.data.remote.firebase_rest

import com.ahmedzenhom.mytasks.data.model.TasksSnapshotModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface FirebaseRestApiService {

    @GET("{userId}.json")
    suspend fun getLastTasksSnapshot(@Path("userId") userId: String): TasksSnapshotModel

    @PUT("{userId}.json")
    suspend fun setLastTasksSnapshot(
        @Path("userId") userId: String,
        @Body request: TasksSnapshotModel
    )

}