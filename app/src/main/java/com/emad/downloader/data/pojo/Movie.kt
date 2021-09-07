package com.emad.downloader.data.pojo

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Movie(
    val id: Int,
    val name: String,
    val type: String,
    val url: String,
    var status: String,
    var isDownloading: Boolean= false
): Parcelable