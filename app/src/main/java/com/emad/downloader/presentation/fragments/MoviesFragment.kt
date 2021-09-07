package com.emad.downloader.presentation.fragments

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.emad.downloader.R
import com.emad.downloader.data.pojo.Movie
import com.emad.downloader.databinding.FragmentMoviesBinding
import com.emad.downloader.presentation.adapters.MoviesAdapter
import com.emad.downloader.presentation.extentions.showLongSnackBar
import com.emad.downloader.presentation.listeners.IMovie
import com.emad.downloader.presentation.viewmodel.MoviesViewModel
import com.emad.downloader.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MoviesFragment : Fragment(), IMovie {
    val moviesViewModel: MoviesViewModel by viewModels()
    lateinit var mBinding: FragmentMoviesBinding
    lateinit var currentMovie: Movie
    lateinit var downloadManager: DownloadManager
    @Inject
    lateinit var adapter: MoviesAdapter
    var downloadMap: HashMap<Long, Movie> = HashMap()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.setIMovieListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMoviesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        initMoviesRecyclerView()
        observeMovies()
    }

    private fun initMoviesRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        mBinding.moviesRecyclerView.layoutManager = layoutManager
        mBinding.moviesRecyclerView.adapter = adapter
    }

    private fun observeMovies() {
        lifecycleScope.launchWhenStarted {
            moviesViewModel.getMovies()
            moviesViewModel.moviesStateFlow.collect {
                when (it) {
                    is Resource.Success -> {
                        adapter.submitList(it.data!!)
                    }
                }
            }
        }
    }

    private fun downloadFile(movie: Movie) {
        downloadManager= requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(movie.url))
            .setTitle(movie.name)
            .setDescription(getString(R.string.downloading))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setDestinationInExternalFilesDir(requireContext(), Environment.DIRECTORY_DOWNLOADS, "")
            .setAllowedOverRoaming(true)
        val downloadID = downloadManager.enqueue(request)
        downloadMap.put(downloadID, movie)
        observeDownloadStatus(downloadID)
    }

    @SuppressLint("Range")
    private fun observeDownloadStatus(downloadID: Long){
        var isDownloading = true
        var progress = 0f
        GlobalScope.launch(IO) {
            while (isDownloading) {
                try{
                    val query = DownloadManager.Query()
                    query.setFilterById(downloadID)
                    val cursor = downloadManager.query(query)
                    cursor.moveToFirst()
                    val bytes_Downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    progress = bytes_Downloaded.toFloat() / bytes_total.toFloat() * 100
                    withContext(Main) {
                        delay(2000)
                        if (bytes_total>0)
                            adapter.setMovieStatus(downloadMap.get(downloadID)!!, getString(R.string.downloading) + " " + progress.toInt()+" %", true)
                        else
                            adapter.setMovieStatus(downloadMap.get(downloadID)!!, getString(R.string.downloading), true)

                    }
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        withContext(Main) {
                            adapter.setMovieStatus(downloadMap.get(downloadID)!!, getString(R.string.downloaded) ,false)
                        }
                        isDownloading = false
                    }
                }catch (ex: Exception){
                }
            }
        }
    }
    override fun selectedMovie(movie: Movie) {
        currentMovie = movie
        downloadFile(movie)
    }
}