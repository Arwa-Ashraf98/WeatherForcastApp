package com.example.weatherforecastapp.ui.alerts.alertServices

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.*
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.data.models.ModelRoot
import com.example.weatherforecastapp.data.repository.Repo
import com.example.weatherforecastapp.data.source.local.LocalSource
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import com.example.weatherforecastapp.data.source.remote.RemoteSource
import com.example.weatherforecastapp.ui.alerts.alertUtils.AlertConverters.convertDateToLong
import com.example.weatherforecastapp.utils.NetworkConnectivityObserver
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlertPeriodicWorkManager(private val context: Context, workParams: WorkerParameters) :
    CoroutineWorker(context, workParams) {

    private val repo = Repo.getRepoInstance(
        localSource = LocalSource.getLocalSourceInstance(),
        remoteSource = RemoteSource.getRemoteSourceInstance()
    )
    private var delay : Long = 0L


    override suspend fun doWork(): Result {
        if (!isStopped) {
            val data = inputData
            val id = data.getLong("id", 0)
            val delay = data.getLong("delay" , 0L)
            this.delay = delay
            getWeatherDate(id.toInt())
        }
        return Result.success()
    }

    private suspend fun getWeatherDate(id: Int) {
        val flow = repo.getAlertById(id)
        if (NetworkConnectivityObserver.isOnline(context)) {
            val weatherFlow = repo.getWeatherDataOnline(
                lat = MySharedPreferences.getLatitude() as Double,
                long = MySharedPreferences.getLongitude() as Double
            )
            flow.collect { alert ->
                Log.e("LIMIT", "TIME LIMMMMMMMMMMMMMT" + checkTimeLimit(alert).toString())
                Log.e("LIMIT", "EQUAAAAAAAAAAAAAAAAL" + ((alert.id) == id).toString())
                if (checkTimeLimit(alert)) {
//                    val delay: Long = getDelay(alert)
                    weatherFlow.collect {
                        if (it.isSuccessful) {
                            val currentWeather = it.body()
                            handleAlertList(currentWeather, delay, alert.id as Int)

                        } else {
                            // what should if response failed TODO
                            Toast.makeText(
                                context,
                                "Failed To Make Call ${it.errorBody()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else {
                    // delete alert
                    repo.deleteAlert(alert)
                    WorkManager.getInstance().cancelAllWorkByTag("$id")
                }
            }

        } else {
            val flowCurrentWeatherLocal = repo.getWeatherDataLocally()
            flow.collect {
                if (checkTimeLimit(it)) {
//                    val delay = getDelay(it)
                    flowCurrentWeatherLocal.collect { currentWeather ->
                        handleAlertList(currentWeather, delay, it.id as Int)
                    }
                } else {
                    repo.deleteAlert(it)
                    WorkManager.getInstance().cancelAllWorkByTag("$id")
                }

            }
        }

    }

    private fun handleAlertList(currentWeather: ModelRoot?, delay: Long, id: Int) {
        if (currentWeather?.alerts.isNullOrEmpty()) {
            val description = "There is No Alerts Today and Weather is ${
                currentWeather?.current?.weather?.get(0)?.description as String
            }"
            setOneTimeWorkManager(
                delay,
                id,
                description
            )

        } else {
            val description =
                currentWeather?.alerts?.get(0)?.description.plus("  ") + currentWeather?.alerts?.get(
                    0
                )?.tags?.get(0)
            setOneTimeWorkManager(
                delay, id, description
            )
        }
    }

    private fun setOneTimeWorkManager(delay: Long, id: Int?, description: String) {
        val data = Data.Builder()
        data.putString(context.getString(R.string.des), description)

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
            AlertOneTimeWorkManager::class.java
        ).setInitialDelay(delay, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "$id",
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
    }

//    private fun getDelay(alert: AlertEntity): Long {
//        val hour =
//            TimeUnit.HOURS.toSeconds(Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toLong())
//        val minute =
//            TimeUnit.MINUTES.toSeconds(Calendar.getInstance().get(Calendar.MINUTE).toLong())
//        Log.e("TAG", alert.startTime.toString())
//        Log.e("TAG", (alert.startTime!! - ((hour + minute) - (2 * 3600L))).toString())
//        return alert.startTime!! - ((hour + minute) - (2 * 3600L))
//    }


    private fun checkTimeLimit(alert: AlertEntity): Boolean {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val date = "$day-${month + 1}-$year"
        val dayNow = convertDateToLong(date, context)
        return dayNow >= alert.startDate as Long
                &&
                dayNow <= alert.endDate as Long
    }

}