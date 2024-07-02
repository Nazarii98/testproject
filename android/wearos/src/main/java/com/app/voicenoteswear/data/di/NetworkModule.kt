package com.app.voicenoteswear.data.di

import com.app.voicenoteswear.data.auth.endpoint.AuthApi
import com.app.voicenoteswear.data.datastorage.UserDataStorage
import com.app.voicenoteswear.data.record.endpoint.RecordApi
import com.app.voicenoteswear.utils.Constant
import com.app.voicenoteswear.utils.Constant.STAGE_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("Default")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(STAGE_BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30,TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .retryOnConnectionFailure(true)

//        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.HEADERS)
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
            builder.addInterceptor(logging)
//        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideNetworkInterceptor(userDataStorage: UserDataStorage): Interceptor {
        return Interceptor {
            val request = it.request().newBuilder()
            val accessToken = userDataStorage.accessToken
            if (accessToken.isNotEmpty()) {
                request.addHeader(
                    Constant.AUTHORIZATION,
                    "${Constant.BEARER} $accessToken"
                )
            }
            it.proceed(request.build())
        }
    }

    @Provides
    @Singleton
    fun provideAuthApi(@Named("Default") retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideRecordApi(@Named("Default") retrofit: Retrofit): RecordApi = retrofit.create(RecordApi::class.java)

}