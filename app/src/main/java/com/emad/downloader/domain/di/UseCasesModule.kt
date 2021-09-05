package com.emad.downloader.domain.di

import com.emad.downloader.data.repositories.MoviesRepository
import com.emad.downloader.domain.usecases.getmovies.GetMoviesUseCase
import com.emad.downloader.domain.usecases.getmovies.GetMoviesUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UseCasesModule {
    @Singleton
    @Provides
    fun provideMovies(moviesRepository: MoviesRepository): GetMoviesUseCase =
        GetMoviesUseCaseImpl(moviesRepository)
}