package com.emad.downloader.presentation.extentions

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun Fragment.showLongSnackBar(text: String, @BaseTransientBottomBar.Duration length: Int = Snackbar.LENGTH_LONG) {
    view?.run { Snackbar.make(this, text, length).show() }
}