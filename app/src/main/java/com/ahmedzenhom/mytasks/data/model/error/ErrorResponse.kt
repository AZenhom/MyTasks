package com.ahmedzenhom.mytasks.data.model.error

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ErrorResponse(
    @SerializedName("error")
    val error: String? = null,
): Serializable