package com.example.safevolume

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var audioManager: AudioManager
    private lateinit var volumeText: TextView
    private lateinit var monitorButton: Button
    private lateinit var thresholdSeekBar: SeekBar

    private var loudThreshold: Int = 12  // Default loud threshold
    private var monitoring = false      // To track monitoring state

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views and AudioManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        volumeText = findViewById(R.id.volumeText)
        monitorButton = findViewById(R.id.monitorButton)
        thresholdSeekBar = findViewById(R.id.thresholdSeekBar)

        // Configure SeekBar
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        thresholdSeekBar.max = maxVolume
        thresholdSeekBar.progress = loudThreshold

        // Display initial threshold and volume
        updateVolumeText()

        // Listener for SeekBar
        thresholdSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                loudThreshold = progress
                updateVolumeText()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Listener for monitoring button
        monitorButton.setOnClickListener {
            toggleMonitoring()
        }
    }

    private fun updateVolumeText() {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeText.text = "Volume: $currentVolume / ${thresholdSeekBar.max}\nThreshold: $loudThreshold"
    }

    private fun toggleMonitoring() {
        monitoring = !monitoring
        if (monitoring) {
            monitorButton.text = "Stop Monitoring"
            checkVolume()
        } else {
            monitorButton.text = "Start Monitoring"
        }
    }

    private fun checkVolume() {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        updateVolumeText()

        if (currentVolume > loudThreshold) {
            showLoudAlert()
        }

        // Continue monitoring if enabled
        if (monitoring) {
            monitorButton.postDelayed({ checkVolume() }, 1000)  // Check every second
        }
    }

    private fun showLoudAlert() {
        AlertDialog.Builder(this)
            .setTitle("Volume Alert")
            .setMessage("The music is too loud! Please lower the volume.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}

