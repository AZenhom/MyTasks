package com.ahmedzenhom.mytasks.data.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BaseResponse<T> constructor(
        @SerializedName("data") val data: T
): Serializable
