package com.ahmedzenhom.mytasks.data.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthServerRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseRestRetrofit