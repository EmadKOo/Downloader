package com.emad.downloader.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.emad.downloader.R
import com.emad.downloader.data.repositories.MoviesRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}