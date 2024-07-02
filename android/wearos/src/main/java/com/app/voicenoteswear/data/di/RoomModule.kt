package com.app.voicenoteswear.data.di

import android.content.Context
import androidx.room.Room
import com.app.voicenoteswear.data.database.AppDatabase
import com.app.voicenoteswear.data.database.FilePathDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            AppDatabase::class.java, "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFilePathDao(database: AppDatabase): FilePathDao {
        return database.filePathDao()
    }
}