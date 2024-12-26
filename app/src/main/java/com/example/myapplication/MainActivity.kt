package com.example.safevolume

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.safevolume.R

class MainActivity : AppCompatActivity() {

    private lateinit var audioManager: AudioManager
    private lateinit var volumeText: TextView
    private lateinit var monitorButton: Button
    private lateinit var thresholdSeekBar: SeekBar
    private val maxVolume: Int = 15  // Set this to the maximum volume level for your device
    private var loudThreshold: Int = 12  // Volume level threshold to trigger an alert

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        volumeText = findViewById(R.id.volumeText)
        monitorButton = findViewById(R.id.monitorButton)
        thresholdSeekBar = findViewById(R.id.thresholdSeekBar)

        thresholdSeekBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        thresholdSeekBar.progress = loudThreshold

        thresholdSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                loudThreshold = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        monitorButton.setOnClickListener {
            startMonitoringVolume()
        }
    }

    private fun startMonitoringVolume() {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        volumeText.text = "Volume: $currentVolume/$maxVolume"

        // Check if the volume exceeds the loud threshold
        if (currentVolume > loudThreshold) {
            showLoudAlert()
        }
    }

    private fun showLoudAlert() {
        Toast.makeText(this, "The music is too loud! Lower the volume.", Toast.LENGTH_SHORT).show()
    }
}
