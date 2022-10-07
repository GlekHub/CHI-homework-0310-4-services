package com.glekhub.chi_homework_0610_4_2

import android.content.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.glekhub.chi_homework_0610_4_2.databinding.ActivityMainBinding
import com.glekhub.chi_homework_0610_4_2.services.SoundService

class MainActivity : AppCompatActivity() {


    private val receiver = SeekBarReceiver()
    private val filter = IntentFilter()
    var sService: SoundService? = null
    private var sBound: Boolean = false

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, process: Int, fromUser: Boolean) {
                if (fromUser) sService!!.seekBarChange(process)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        binding.serviceButton.setOnClickListener { onButtonClick() }

        filter.addAction("TRACK_PROGRESS")
        registerReceiver(receiver, filter)

    }

    override fun onStart() {
        super.onStart()
        Intent(this, SoundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun updateSeekBar(progress: Int) {
        binding.seekBar.setProgress(progress, false)
    }

    private fun onButtonClick() {
        if (sBound) {
            sService!!.startSound()
            sService!!.notification()
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as SoundService.SoundBinder
            sService = binder.getService()
            sBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            sBound = false
        }
    }


    inner class SeekBarReceiver : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onReceive(context: Context?, intent: Intent?) {
            val progress: Int
            val action = intent?.action
            if (action.equals("TRACK_PROGRESS")) {
                progress = intent?.getIntExtra("progress", 0) ?: 0
                updateSeekBar(progress)
            }
        }
    }
}