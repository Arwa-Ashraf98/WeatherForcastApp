package com.example.weatherforecastapp.ui.alerts.alertServices

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherforecastapp.R

class AlertOneTimeWorkManager(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {


    override suspend fun doWork(): Result {
        val description = inputData.getString(context.getString(R.string.des))
        startAlertService(description as String)
        return Result.success()
    }

    private fun startAlertService(des: String) {
        val intent = Intent(applicationContext, AlertService::class.java)
        intent.putExtra(context.getString(R.string.des), des)
        ContextCompat.startForegroundService(context , intent)
    }
}