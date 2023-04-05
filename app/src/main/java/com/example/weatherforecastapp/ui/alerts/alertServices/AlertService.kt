package com.example.weatherforecastapp.ui.alerts.alertServices

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.ui.main.MainActivity
import com.example.weatherforecastapp.utils.Constants


class AlertService : Service() {

    private var notificationManager: NotificationManager? = null
    private var alertWindowManger: AlertWindowManager? = null
    private val sound =
        Uri.parse("C:\\Users\\Arwa\\AndroidStudioProjects\\WeatherForecastApp\\app\\src\\main\\res\\raw\\notification.mp3")


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // get data from work manager
        val description = intent?.getStringExtra("description")

        // Create Notification Channel
        createNotificationChannel()
        startForeground(Constants.FOREGROUND_ID, makeNotification(description!!))
        if (Settings.canDrawOverlays(this)) {
            alertWindowManger = AlertWindowManager(this, description)
            alertWindowManger!!.initializeWindowManager()
        }

        val dialogIntent = Intent(this, MainActivity::class.java)
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(dialogIntent)

        return START_NOT_STICKY
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun makeNotification(description: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        return NotificationCompat.Builder(applicationContext, "${Constants.CHANNEL_ID}")
            .setSmallIcon(R.drawable.season)
            .setContentText(description)
            .setContentTitle(getString(R.string.weather_alarm))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(description)
            ).setVibrate(longArrayOf(1000, 500, 1000, 1000))
            .setAutoCancel(true)
            .setLights(Color.BLUE, 3000, 3000)
            .setSound(sound)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        val name: String = getString(R.string.channel)
        val description = getString(R.string.description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("${Constants.CHANNEL_ID}", name, importance)
        channel.enableVibration(true)
        channel.description = description
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        channel.setSound(sound, attributes)
        notificationManager = this.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}