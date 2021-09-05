package com.emad.downloader.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emad.downloader.data.pojo.Movie
import com.emad.downloader.domain.usecases.getmovies.GetMoviesUseCase
import com.emad.downloader.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(private val getMoviesUseCase: GetMoviesUseCase) :
    ViewModel() {

    private val _moviesStateFlow= MutableStateFlow<Resource<List<Movie>>>(Resource.Init())
    val moviesStateFlow: StateFlow<Resource<List<Movie>>> = _moviesStateFlow


    fun getMovies()= viewModelScope.launch {
        try{
        _moviesStateFlow.emit(Resource.Loading())
        _moviesStateFlow.emit(Resource.Success(getMoviesUseCase.invoke()))
    }catch (ex:Exception){
        _moviesStateFlow.emit(Resource.Error(ex.localizedMessage))
    }}

}