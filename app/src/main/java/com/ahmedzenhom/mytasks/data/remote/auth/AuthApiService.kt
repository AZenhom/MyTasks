package com.ahmedzenhom.mytasks.data.remote.auth

import com.ahmedzenhom.mytasks.data.model.AccountModel
import com.ahmedzenhom.mytasks.data.model.response.BaseResponse
import retrofit2.http.*

interface AuthApiService {

    @GET("user")
    suspend fun getUserIfExists(@Query("phone") phone: String): BaseResponse<AccountModel?>

    @POST("user")
    suspend fun registerUser(@Body request: AccountModel)

}