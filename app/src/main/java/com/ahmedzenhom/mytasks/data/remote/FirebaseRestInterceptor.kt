package com.ahmedzenhom.mytasks.data.remote

import com.ahmedzenhom.mytasks.data.local.datastore.AccountDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class FirebaseRestInterceptor @Inject constructor(
    private val accountDataStore: AccountDataStore,
) : Interceptor {

    private var accessToken: String? = null

    init {
        CoroutineScope(Dispatchers.IO).launch {
            accountDataStore.getAccessToken().collect {
                accessToken = it
            }
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
        if (accessToken != null) {
            val originalUrl = chain.request().url
            val newUrl = originalUrl
                .newBuilder()
                .addQueryParameter("access_token", accessToken)
                .build()
            request.url(newUrl)
            request = request.addHeader("Authorization", "Bearer $accessToken")
        }
        return chain.proceed(request.build())
    }
}

