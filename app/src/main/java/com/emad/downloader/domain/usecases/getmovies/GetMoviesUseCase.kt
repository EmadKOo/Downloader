package com.emad.downloader.domain.usecases.getmovies

import com.emad.downloader.data.pojo.Movie

interface GetMoviesUseCase {
    suspend operator fun invoke(): List<Movie>
}