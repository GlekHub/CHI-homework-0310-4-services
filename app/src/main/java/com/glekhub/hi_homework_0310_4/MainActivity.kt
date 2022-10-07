package com.glekhub.hi_homework_0310_4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.glekhub.hi_homework_0310_4.databinding.ActivityMainBinding
import com.glekhub.hi_homework_0310_4.services.SoundService
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private val formatter = SimpleDateFormat("mm:ss", Locale.US)

    private lateinit var soundService: Intent
    private val filter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        soundService = Intent(applicationContext, SoundService::class.java)

        binding.serviceButton.setOnClickListener { startService(soundService) }

        filter.addAction("TRACK_PROGRESS")
        registerReceiver(receiver, filter)

    }


    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            if (action.equals("TRACK_PROGRESS")) {
                val millis = p1?.getIntExtra("progress", -1)
                val time = formatter.format(millis).toString()
                binding.timeTv.text = time
                Log.d("TAG", time)
            }
        }
    }

}