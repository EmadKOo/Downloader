package com.emad.downloader.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emad.downloader.data.pojo.Movie
import com.emad.downloader.databinding.MovieItemLayoutBinding
import javax.inject.Inject

class MoviesAdapter @Inject constructor() : RecyclerView.Adapter<MoviesAdapter.MyViewHolder>() {
    val list = ArrayList<Movie>()

    open inner class MyViewHolder(private val mBinding: MovieItemLayoutBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(movie: Movie) {
            mBinding.title = movie.name
            if (movie.type.equals("VIDEO")) mBinding.isBook= false else mBinding.isBook= true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            MovieItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun submitList(newList: List<Movie>) {
        list.clear()
        list.addAll(newList)
        notifyItemRangeInserted(0, newList.size - 1)
    }
}