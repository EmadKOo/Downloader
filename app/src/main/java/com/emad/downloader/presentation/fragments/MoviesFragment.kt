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
import com.emad.downloader.presentation.listeners.IMovie
import com.emad.downloader.presentation.viewmodel.MoviesViewModel
import com.emad.downloader.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.File
import javax.inject.Inject


private const val TAG = "MoviesFragment"
@AndroidEntryPoint
class MoviesFragment : Fragment(), IMovie {
    val moviesViewModel: MoviesViewModel by viewModels()
    lateinit var mBinding: FragmentMoviesBinding
    lateinit var currentMovie: Movie
    @Inject
    lateinit var adapter: MoviesAdapter
    private val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context, intent: Intent) {
            val id: Long = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            adapter.setMovieStatus(currentMovie, getString(R.string.downloaded))
            Log.d(TAG, "onReceive: " + id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.setIMovieListener(this)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding= FragmentMoviesBinding.inflate(inflater, container, false)
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
    private fun observeMovies(){
        Log.d(TAG, "observeMovies: ")
        lifecycleScope.launchWhenStarted {
            moviesViewModel.getMovies()
            moviesViewModel.moviesStateFlow.collect{
                when(it){
                    is Resource.Error -> { }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                       adapter.submitList(it.data!!)
                    }
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun downloadFile(movie: Movie){

        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(movie.url))
            .setTitle(movie.name)
            .setDescription(getString(R.string.downloading))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setDestinationInExternalFilesDir( requireContext(),
                Environment.DIRECTORY_DOWNLOADS,
                "")
            .setAllowedOverRoaming(true)

        val downloadId: Long = downloadManager.enqueue(request)
        Log.d(TAG, "downloadFile: " + downloadId)

        requireActivity().registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(onComplete)
    }

    override fun selectedMovie(movie: Movie) {
        currentMovie= movie
        downloadFile(movie)
        adapter.setMovieStatus(movie, getString(R.string.downloading))
    }
}