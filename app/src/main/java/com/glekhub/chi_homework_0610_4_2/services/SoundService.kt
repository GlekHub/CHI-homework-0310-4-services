package com.glekhub.chi_homework_0610_4_2.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.glekhub.chi_homework_0610_4_2.R


class SoundService : Service() {

    private val channelId = "sound_channel"
    private val binder = SoundBinder()

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private var progress = 0
    private var progressDr = 0

    fun seekBarChange(pr: Int) {
        progressDr = pr * mediaPlayer!!.duration / 100
        mediaPlayer!!.seekTo(progressDr)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("ABBA", "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ABBA", "onStart")
        return START_STICKY
    }

    fun startSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer!!.reset()

        assets.openFd("sound3.mp3").use { asset ->
            mediaPlayer!!.setDataSource(asset.fileDescriptor, asset.startOffset, asset.length)
        }

        mediaPlayer!!.prepare()
        mediaPlayer!!.start()

        val thread = Thread {
            while (mediaPlayer!!.isPlaying) {
                progress = 100 * mediaPlayer!!.currentPosition / mediaPlayer!!.duration
                Intent().also { intent ->
                    intent.action = "TRACK_PROGRESS"
                    intent.putExtra("progress", progress)
                    sendBroadcast(intent)
                }
                Thread.sleep(1000)
            }
        }
        thread.start()

    }


    override fun onBind(intent: Intent): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "MySound")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }


    inner class SoundBinder : Binder() {
        fun getService(): SoundService = this@SoundService
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    fun notification() {

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
            .setContentText("sound3.mp3")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_launcher_foreground
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(100, notification)
    }
}