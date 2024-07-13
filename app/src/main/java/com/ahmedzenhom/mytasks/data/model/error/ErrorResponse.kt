package com.ahmedzenhom.mytasks.data.model.error

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("errors")
    val errors: List<ErrorResponseItem>? = null
)

data class ErrorResponseItem(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("message")
    val message: String? = null,
)