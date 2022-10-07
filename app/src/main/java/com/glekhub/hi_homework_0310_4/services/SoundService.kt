package com.glekhub.hi_homework_0310_4.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.glekhub.hi_homework_0310_4.R
import java.util.*


class SoundService : Service() {

    private val channelId = "sound_channel"

    private val filter = IntentFilter()

    override fun onCreate() {
        super.onCreate()
        Log.d("ABBA", "onCreate")

        filter.addAction("TRACK_PROGRESS")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ABBA", "onStart")

        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.sound2)
        mediaPlayer.start()

        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    Intent().also {
                        it.action = "TRACK_PROGRESS"
                        it.putExtra("progress", mediaPlayer.currentPosition)
                        sendBroadcast(it)
                    }
                }
            }
        }, 0, 1000)

        notification()

        return super.onStartCommand(intent, flags, startId)

    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun notification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Demo Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, SoundService::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Foreground Service")
            .setContentText("Sound Service")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(100, notification)
    }
}