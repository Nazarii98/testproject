package com.app.voicenoteswear.data.di

import android.content.Context
import android.content.SharedPreferences
import com.app.voicenoteswear.data.datastorage.SharedPrefsStorage
import com.app.voicenoteswear.data.datastorage.UserDataStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.packageName + "_preferences",
            Context.MODE_PRIVATE
        )
    }

    @Singleton
    @Provides
    fun provideSharedPrefsStorage(sharedPreferences: SharedPreferences): SharedPrefsStorage {
        return SharedPrefsStorage(sharedPreferences)
    }

    @Singleton
    @Provides
    fun provideUserDataStorage(sharedPrefsStorage: SharedPrefsStorage): UserDataStorage {
        return UserDataStorage(sharedPrefsStorage)
    }
}