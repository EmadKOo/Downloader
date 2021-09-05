package com.emad.downloader.data.repositories

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type
import javax.inject.Inject
import com.emad.downloader.data.pojo.Movie


class MoviesRepository @Inject constructor(@ApplicationContext private val context: Context){
     fun getMovies(): List<Movie> {
         val inputStream: InputStream = context.assets.open("movies.json")
         val bufferedReader = BufferedReader(InputStreamReader(inputStream))
         val collectionType: Type = object : TypeToken<List<Movie?>?>() {}.type
         return Gson().fromJson(bufferedReader, collectionType) as List<Movie>
     }
}