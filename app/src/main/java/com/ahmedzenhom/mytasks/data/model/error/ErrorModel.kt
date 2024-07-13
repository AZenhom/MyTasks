package com.ahmedzenhom.mytasks.data.model.error

sealed class ErrorModel : Exception() {
    object Unknown : ErrorModel()
    object Connection : ErrorModel()
    class Network(val code: Int, val serverMessage: String?) : ErrorModel()
}