package com.ahmedzenhom.mytasks.data.di

import com.ahmedzenhom.mytasks.BuildConfig
import com.ahmedzenhom.mytasks.data.local.datastore.AccountDataStore
import com.ahmedzenhom.mytasks.data.remote.BasicHeaderInterceptor
import com.ahmedzenhom.mytasks.data.remote.FirebaseRestInterceptor
import com.ahmedzenhom.mytasks.data.remote.auth.AuthApiService
import com.ahmedzenhom.mytasks.data.remote.firebase_rest.FirebaseRestApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    companion object {
        const val AUTH_SERVER_ADDRESS = BuildConfig.AUTH_SERVER_ADDRESS
        const val AUTH_BASE_URL = BuildConfig.AUTH_BASE_URL
        const val FIREBASE_REST_ADDRESS = BuildConfig.FIREBASE_REST_ADDRESS
        const val TASKS_BASE_URL = BuildConfig.TASKS_BASE_URL
    }

    @Singleton
    @Provides
    fun getFirebaseRestInterceptor(
        accountDataStore: AccountDataStore,
    ): FirebaseRestInterceptor {
        return FirebaseRestInterceptor(accountDataStore)
    }

    @Singleton
    @Provides
    fun getBasicHeaderInterceptor(): BasicHeaderInterceptor {
        return BasicHeaderInterceptor()
    }

    @Singleton
    @Provides
    @Named("FirebaseRestHttpClient")
    fun getFirebaseOkHttpClient(headerInterceptor: FirebaseRestInterceptor): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .addInterceptor(headerInterceptor)
            .build()
    }

    @Singleton
    @Provides
    @Named("BasicOkHttpClient")
    fun getBasicOkHttpClient(headerInterceptor: BasicHeaderInterceptor): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .addInterceptor(headerInterceptor)
            .build()
    }

    @Singleton
    @Provides
    @FirebaseRestRetrofit
    fun getFirebaseRestRetrofit(@Named("FirebaseRestHttpClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FIREBASE_REST_ADDRESS + TASKS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    @AuthServerRetrofit
    fun getAuthServerRetrofit(@Named("BasicOkHttpClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AUTH_SERVER_ADDRESS + AUTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun getAuthService(@AuthServerRetrofit retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    fun getFirebaseRestService(@FirebaseRestRetrofit retrofit: Retrofit): FirebaseRestApiService {
        return retrofit.create(FirebaseRestApiService::class.java)
    }

}