package com.emad.downloader.presentation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.emad.downloader.R
import com.emad.downloader.databinding.FragmentMoviesBinding
import com.emad.downloader.presentation.adapters.MoviesAdapter
import com.emad.downloader.presentation.viewmodel.MoviesViewModel
import com.emad.downloader.utils.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type
import javax.inject.Inject

private const val TAG = "MoviesFragment"
@AndroidEntryPoint
class MoviesFragment : Fragment() {
    val moviesViewModel: MoviesViewModel by viewModels()
    lateinit var mBinding: FragmentMoviesBinding
    @Inject
    lateinit var adapter: MoviesAdapter
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
}