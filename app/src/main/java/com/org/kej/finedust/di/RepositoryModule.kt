package com.org.kej.finedust.di

import com.org.kej.finedust.domain.Repository
import com.org.kej.finedust.data.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun provideRepositoryImpl(repository: RepositoryImpl): Repository
}