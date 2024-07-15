package com.ahmedzenhom.mytasks.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class BasicHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
        return chain.proceed(request.build())
    }
}

