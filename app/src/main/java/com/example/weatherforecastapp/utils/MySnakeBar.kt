package com.example.weatherforecastapp.utils

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

object MySnakeBar {
    private lateinit var snackbar: Snackbar
    fun showSnakeBar(
        view: View,
        context: Context,
        message: String,
        duration: Int,
        textColor: Int,
        backgroundColor: Int
    ): Snackbar {
        snackbar = Snackbar
            .make(context, view, message, duration)
            .setBackgroundTint(backgroundColor)
            .setActionTextColor(textColor)
        snackbar.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
        return snackbar
    }
}