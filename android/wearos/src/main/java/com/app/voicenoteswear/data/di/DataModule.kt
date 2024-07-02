package com.app.voicenoteswear.data.di

import com.app.voicenoteswear.domain.auth.repository.AuthRepository
import com.app.voicenoteswear.data.auth.repository.AuthRepositoryImpl
import com.app.voicenoteswear.data.record.repository.RecordRepositoryImpl
import com.app.voicenoteswear.domain.record.repository.RecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun provideAuthRepository(repositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun provideRecordRepository(repositoryImpl: RecordRepositoryImpl): RecordRepository

}