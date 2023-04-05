package com.example.weatherforecastapp.ui.alerts.alertServices

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.databinding.DialogAlarmBinding

class AlertWindowManager(private val context: Context, private val description: String) {
    private lateinit var windowManager: WindowManager
    private lateinit var view: View
    private lateinit var binding: DialogAlarmBinding

    fun initializeWindowManager() {
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.dialog_alarm, null)
        binding = DialogAlarmBinding.bind(view)

        binding.textViewDescription.text = description
        binding.btnOk.setOnClickListener {
            closeWindowManager()
            closeSystemService()
        }
        initView()
    }

    private fun initView() {
        val layoutFlag =
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        val params = WindowManager.LayoutParams(
            width,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE,
            PixelFormat.TRANSLUCENT
        )
        windowManager.addView(view, params)

    }


    private fun closeWindowManager() {
        try {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(view)
            view.invalidate()
            (view.parent as ViewGroup).removeAllViews()
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }

    private fun closeSystemService() {
        context.stopService(Intent(context, AlertService::class.java))
    }
}