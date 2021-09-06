package com.emad.downloader.presentation.listeners

import com.emad.downloader.data.pojo.Movie

interface IMovie {
    fun selectedMovie(movie: Movie)
}