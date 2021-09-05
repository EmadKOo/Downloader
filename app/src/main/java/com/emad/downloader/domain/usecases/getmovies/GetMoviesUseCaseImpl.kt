package com.emad.downloader.domain.usecases.getmovies

import com.emad.downloader.data.pojo.Movie
import com.emad.downloader.data.repositories.MoviesRepository
import javax.inject.Inject

class GetMoviesUseCaseImpl @Inject constructor(private val moviesRepository: MoviesRepository): GetMoviesUseCase {
    override suspend fun invoke(): List<Movie> = moviesRepository.getMovies()
}