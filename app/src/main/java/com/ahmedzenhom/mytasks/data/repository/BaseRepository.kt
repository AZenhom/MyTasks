package com.ahmedzenhom.mytasks.data.repository

import android.util.Log
import com.ahmedzenhom.mytasks.data.model.error.ErrorModel
import com.ahmedzenhom.mytasks.data.model.error.ErrorResponse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

abstract class BaseRepository {

    companion object {
        val errorType = object : TypeToken<ErrorResponse>() {}.type
    }

    suspend fun <T> execute(job: suspend () -> T): T = withContext(Dispatchers.IO) {
        try {
            return@withContext job()
        } catch (e: Exception) {
            Log.e("BaseRepository", "Error: " + e.message, e)
            when (e) {
                is IOException -> throw ErrorModel.Connection
                is HttpException -> throw getHttpErrorMessage(e)
            }
            throw e
        }
    }

    private fun getHttpErrorMessage(httpException: HttpException): Exception {
        return try {
            FirebaseCrashlytics.getInstance().recordException(httpException)
            val response = httpException.response()!!.errorBody()!!.string()
            val errorResponse: ErrorResponse = Gson().fromJson(response, errorType)
            if (!errorResponse.error.isNullOrEmpty()) {
                return ErrorModel.Network(
                    httpException.code(),
                    errorResponse.error
                )
            } else
                return ErrorModel.Unknown
        } catch (e: Exception) {
            ErrorModel.Unknown
        }
    }
}